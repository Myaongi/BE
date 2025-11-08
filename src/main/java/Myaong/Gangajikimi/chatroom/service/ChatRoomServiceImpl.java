package Myaong.Gangajikimi.chatroom.service;

import Myaong.Gangajikimi.chatmessage.repository.ChatMessageRepository;
import Myaong.Gangajikimi.chatroom.converter.ChatRoomConverter;
import Myaong.Gangajikimi.chatroom.entity.ChatRoom;
import Myaong.Gangajikimi.chatroom.repository.ChatRoomRepository;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomCreateRequest;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomListResponse;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomResponse;
import Myaong.Gangajikimi.common.enums.ChatContext;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.common.util.TimeUtil;
import Myaong.Gangajikimi.matchingpost.entity.MatchingPost;
import Myaong.Gangajikimi.matchingpost.repository.MatchingPostRepository;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.member.repository.MemberRepository;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static Myaong.Gangajikimi.common.response.ErrorCode.MEMBER_NOT_FOUND;
import static Myaong.Gangajikimi.common.response.ErrorCode.NOT_SAME_PEOPLE;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final ChatRoomConverter converter;
	private final ChatMessageRepository chatMessageRepository;
	private final PostFoundRepository postFoundRepository;
	private final PostLostRepository postLostRepository;
	private final MatchingPostRepository matchingPostRepository;

	@Override
	@Transactional
	public ChatRoomResponse createChatRoom(ChatRoomCreateRequest req, Long memberId) {
		// 1) 참여자 조회
		Member member1 = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(MEMBER_NOT_FOUND));
		Member member2 = memberRepository.findById(req.getMemberId())
			.orElseThrow(() -> new GeneralException(MEMBER_NOT_FOUND));

		// 2) 자기 자신과 채팅 불가 (ID 기준 비교)
		if (member1.getId().equals(member2.getId())) {
			throw new GeneralException(NOT_SAME_PEOPLE);
		}

		if (req.getMatchingId() != null) {
			MatchingPost mp = matchingPostRepository.findById(req.getMatchingId())
					.orElseThrow(() -> new GeneralException(ErrorCode.NO_MATCHING_FOUND));

			PostLost  lost  = mp.getPostLost();
			PostFound found = mp.getPostFound();

			boolean iAmLostOwner  = lost.getMember().getId().equals(memberId);
			boolean iAmFoundOwner = found.getMember().getId().equals(memberId);

			if (!iAmLostOwner && !iAmFoundOwner) {
				throw new GeneralException(ErrorCode.INVALID_ACCESS);
			}

			// 컨텍스트 기준은 항상 LOST
			PostType ctxType = PostType.LOST;
			Long     ctxPostId = lost.getId();

			// 2-1) 기존 방 재사용 or 생성
			ChatRoom room = chatRoomRepository
					.findByMembersAndPost(member1, member2, ctxType, ctxPostId)
					.orElseGet(() -> {
						Member m1 = memberId <= req.getMemberId() ? member1 : member2;
						Member m2 = memberId <= req.getMemberId() ? member2 : member1;

						ChatRoom newRoom = ChatRoom.builder()
								.member1(m1)
								.member2(m2)
								.postType(ctxType)
								.postId(ctxPostId)
								.context(ChatContext.MATCH)   // 매칭 컨텍스트 만 표시
								.matchedPostId(found.getId()) //
								.similarity(mp.getMatchingRatio())
								.build();

						return chatRoomRepository.save(newRoom);
					});

			// 2-2) 상대 카드 데이터 구성(내가 LOST면 상대는 FOUND, 내가 FOUND면 상대는 LOST)
			Long opponentPostId;
			PostType opponentType;
			String title, region, dogType, dogColor;
			String timeAgo = null; // 필요 없으면 제거

			if (iAmLostOwner) {
				opponentPostId = found.getId();
				opponentType   = PostType.FOUND;
				title  = found.getTitle();
				region = found.getFoundRegion();
				dogType = (found.getDogType() != null) ? found.getDogType().getType() : null;
				dogColor = found.getDogColor();
				timeAgo  = TimeUtil.getTimeAgo(found.getFoundTime());
			} else { // iAmFoundOwner
				opponentPostId = lost.getId();
				opponentType   = PostType.LOST;
				title  = lost.getTitle();
				region = lost.getLostRegion();
				dogType = (lost.getDogType() != null) ? lost.getDogType().getType() : null;
				dogColor = lost.getDogColor();
				timeAgo  = TimeUtil.getTimeAgo(lost.getLostTime());
			}

			// 2-3) 응답을 서비스에서 직접 빌드(컨버터 손 안 댐)
			return ChatRoomResponse.builder()
					.chatroomId(room.getId())
					.member1Id(room.getMember1().getId())
					.member2Id(room.getMember2().getId())
					.createdAt(room.getCreatedAt())

					.matchingRatio(mp.getMatchingRatio())
					.opponentPostId(opponentPostId)
					.opponentPostType(opponentType)
					.opponentTitle(title)
					.opponentRegion(region)
					.opponentDogType(dogType)
					.opponentDogColor(dogColor)
					.opponentTimeAgo(timeAgo)
					.build();
		}

		// 3) (회원쌍 + 게시글 컨텍스트) 기준으로 기존 방 조회
		return chatRoomRepository
			.findByMembersAndPost(member1, member2, req.getPostType(), req.getPostId())
			.map(converter::toResponse)
			.orElseGet(() -> {
				// 저장 직전 정규화
				Member m1 = member1.getId() <= member2.getId() ? member1 : member2;
				Member m2 = member1.getId() <= member2.getId() ? member2 : member1;

				ChatRoom newRoom = ChatRoom.builder()
					.member1(m1)
					.member2(m2)
					.postType(req.getPostType())
					.postId(req.getPostId())
					.build();

				return converter.toResponse(chatRoomRepository.save(newRoom));
			});
	}

	// 채팅방 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<ChatRoomListResponse> getChatRooms(Long memberId) {
		return chatRoomRepository.findChatRoomsWithLastMessageAndUnreadCount(memberId);
	}


	@Override
	@Transactional(readOnly = true)
	public ChatRoomResponse getRoomAndMatchCard(Long chatRoomId, Long requesterId) {
		// 1) 방 조회 (+ 권한 체크: requester가 member1/2 중 하나인지)
		ChatRoom room = chatRoomRepository.findById(chatRoomId)
				.orElseThrow(() -> new GeneralException(ErrorCode.CHATROOM_NOT_FOUND));

		Long m1 = room.getMember1().getId();
		Long m2 = room.getMember2().getId();
		if (!m1.equals(requesterId) && !m2.equals(requesterId)) {
			throw new GeneralException(ErrorCode.INVALID_ACCESS);
		}

		// 2) 기본 필드
		ChatRoomResponse.ChatRoomResponseBuilder b = ChatRoomResponse.builder()
				.chatroomId(room.getId())
				.member1Id(m1)
				.member2Id(m2)
				.createdAt(room.getCreatedAt());

		// 3) 매칭 컨텍스트면 카드/유사도 복원 (matchedPostId 필수)
		if (room.getContext() == ChatContext.MATCH && room.getMatchedPostId() != null) {
			Long lostId  = room.getPostId();         // 항상 LOST 기준
			Long foundId = room.getMatchedPostId();  // 상대 FOUND

			PostLost lost  = postLostRepository.findById(lostId).orElse(null);
			PostFound found = postFoundRepository.findById(foundId).orElse(null);

			if (lost != null && found != null) {
				boolean iAmLostOwner = lost.getMember().getId().equals(requesterId);

				Float ratio = matchingPostRepository
						.findByPostLostAndPostFound(lost, found)
						.map(MatchingPost::getMatchingRatio)
						.orElse(null);

				if (iAmLostOwner) { // 내가 LOST 주인 → 상대 FOUND 카드
					b.matchingRatio(ratio)
							.opponentPostId(found.getId())
							.opponentPostType(PostType.FOUND)
							.opponentTitle(found.getTitle())
							.opponentRegion(found.getFoundRegion())
							.opponentDogType(found.getDogType()!=null?found.getDogType().getType():null)
							.opponentDogColor(found.getDogColor())
							.opponentTimeAgo(TimeUtil.getTimeAgo(found.getFoundTime()));
				} else {            // 내가 FOUND 주인 → 상대 LOST 카드
					b.matchingRatio(ratio)
							.opponentPostId(lost.getId())
							.opponentPostType(PostType.LOST)
							.opponentTitle(lost.getTitle())
							.opponentRegion(lost.getLostRegion())
							.opponentDogType(lost.getDogType()!=null?lost.getDogType().getType():null)
							.opponentDogColor(lost.getDogColor())
							.opponentTimeAgo(TimeUtil.getTimeAgo(lost.getLostTime()));
				}
			}
		}

		return b.build();
	}




	// // 채팅방 삭제 (soft)
	// @Override
	// @Transactional
	// public ChatRoomDeleteResponse softDeleteChatRoom(Long chatroomId, Long requesterId) {
	// 	ChatRoom chatRoom = chatRoomRepository.findByIdAndParticipant(chatroomId, requesterId)
	// 		.orElseThrow(() -> new GeneralException(ErrorCode.CHATROOM_NOT_FOUND));
	//
	// 	// 소프트 삭제 플래그 변경
	// 	chatRoom.softDelete(requesterId);
	//
	// 	// 두 명 모두 삭제했으면 메시지까지 완전 삭제
	// 	if (chatRoom.isFullyDeleted()) {
	// 		chatMessageRepository.deleteByChatRoomId(chatroomId);
	// 		chatRoomRepository.delete(chatRoom);
	// 	}
	//
	// 	return converter.toDeleteResponse(chatRoom, requesterId);
	// }
}

