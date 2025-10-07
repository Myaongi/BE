// src/main/java/Myaong/Gangajikimi/sightcard/service/SightCardService.java
package Myaong.Gangajikimi.sightcard.service;

import static Myaong.Gangajikimi.common.response.ErrorCode.*;

import Myaong.Gangajikimi.chatroom.entity.ChatRoom;
import Myaong.Gangajikimi.chatroom.repository.ChatRoomRepository;
import Myaong.Gangajikimi.chatroom.service.ChatRoomService;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomCreateRequest;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomResponse;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.kakaoapi.service.KakaoApiService;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.member.repository.MemberRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.sightcard.converter.SightCardConverter;
import Myaong.Gangajikimi.sightcard.entity.SightCard;
import Myaong.Gangajikimi.sightcard.repository.SightCardRepository;
import Myaong.Gangajikimi.sightcard.web.dto.SightCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SightCardService {

	private final SightCardRepository sightCardRepository;
	private final PostLostRepository postLostRepository;
	private final MemberRepository memberRepository;
	private final KakaoApiService kakaoApiService;
	private final SightCardConverter sightCardConverter;
	private final ChatRoomRepository chatRoomRepository;

	// 채팅방 생성/재사용용
	private final ChatRoomService chatRoomService;

	/** 발견카드 저장 + 채팅방 생성/재사용까지 한 번에 */
	@Transactional
	public SightCardDto.CreateWithChatResponse createWithChat (Long reporterId, SightCardDto.CreateRequest req) {
		// 1) 기본 조회
		PostLost postLost = postLostRepository.findById(req.getPostLostId())
			.orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

		Member reporter = memberRepository.findById(reporterId)
			.orElseThrow(() -> new GeneralException(ErrorCode.MEMBER_NOT_FOUND));

		// 자기 글에는 금지 (정책에 맞는 ErrorCode로 교체 가능)
		if (postLost.getMember().getId().equals(reporter.getId())) {
			throw new GeneralException(ErrorCode.NOT_SAME_PEOPLE);
		}

		// 2) 날짜/시간 파싱
		LocalDate date = toLocalDate(req.getDate());
		LocalTime time = toLocalTimeFromDateArray(req.getTime());

		// 3) 채팅방 생성/재사용
		ChatRoomCreateRequest roomReq = ChatRoomCreateRequest.builder()
			// ChatRoomService 시그니처: (req, memberId)
			// -> memberId = reporter(요청자), req.memberId = 상대(분실글 작성자)
			.memberId(postLost.getMember().getId())
			.postType(PostType.LOST)
			.postId(postLost.getId())
			.build();

		ChatRoomResponse room = chatRoomService.createChatRoom(roomReq, reporterId);

		var existing = sightCardRepository.findByChatRoom_Id(room.getChatroomId());
		if (existing.isPresent()) {
			throw new GeneralException(ALREADY_EXIST_CARD);
		}

		// 4) 역지오코딩
		String place = kakaoApiService.getAddrFromKakaoApi(req.getLongitude(), req.getLatitude());

		// 5) 저장
		SightCard saved = sightCardRepository.save(
			SightCard.builder()
				.postLost(postLost)
				.reporter(reporter)
				.foundDate(date)
				.foundTime(time)
				.longitude(req.getLongitude())
				.latitude(req.getLatitude())
				.foundPlace(place)
				.chatRoom(ChatRoom.builder().id(room.getChatroomId()).build()) // ref only
				.build()
		);

		// 6) 통합 응답
		return sightCardConverter.toCreateWithChatResponse(saved, room);
	}

	@Transactional(readOnly = true)
	public SightCardDto.SightCardResponse getByChatRoom(Long chatRoomId, Long participatorId) {
		// 권한: 방 참가자만
		ChatRoom room = chatRoomRepository.findById(chatRoomId)
			.orElseThrow(() -> new GeneralException(ErrorCode.CHATROOM_NOT_FOUND));

		// 해당 방에 권한이 없으면
		boolean participant = room.getMember1().getId().equals(participatorId)
			|| room.getMember2().getId().equals(participatorId);
		if (!participant) throw new GeneralException(ErrorCode.POST_NO_AUTH);

		// 채팅방 id 를 통해 발견카드 조회
		SightCard card = sightCardRepository.findByChatRoom_Id(chatRoomId)
			.orElseThrow(() -> new GeneralException(ErrorCode.CARD_NOT_FOUND));
		return sightCardConverter.toCreateResponse(card);
	}

	private LocalDate toLocalDate(List<Integer> arr) {
		return LocalDate.of(arr.get(0), arr.get(1), arr.get(2));
	}

	// 프론트 time 배열: [yyyy,MM,dd,HH,mm,ss,(nanos)]
	private LocalTime toLocalTimeFromDateArray(List<Integer> arr) {
		int hour   = arr.size() >= 4 ? arr.get(3) : 0;
		int minute = arr.size() >= 5 ? arr.get(4) : 0;
		int second = arr.size() >= 6 ? arr.get(5) : 0;
		return LocalTime.of(hour, minute, second);
	}
}
