package Myaong.Gangajikimi.notification.service;

import static Myaong.Gangajikimi.common.response.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Myaong.Gangajikimi.common.enums.NotificationType;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.memberlocation.entity.MemberLocation;
import Myaong.Gangajikimi.memberlocation.repository.MemberLocationRepository;
import Myaong.Gangajikimi.notification.converter.NotificationConverter;
import Myaong.Gangajikimi.notification.entity.Notification;
import Myaong.Gangajikimi.notification.repository.NotificationRepository;
import Myaong.Gangajikimi.notification.web.dto.NotificationDto;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final MemberLocationRepository locationRepository;
	private final NotificationRepository notificationRepository;

	/**
	 * 반경 3km 내의 사용자들에게 알림 생성 (PostGIS ST_DWithin 사용)
	 */
	@Transactional
	public void notifyNearbyUsers(Long postId, double postLat, double postLon, long excludeMemberId, PostType postType) {
		List<MemberLocation> nearbyUsers = locationRepository.findWithinRadius(postLat, postLon, 3000.0, excludeMemberId);

		for (MemberLocation loc : nearbyUsers) {
			Notification notification = Notification.builder()
				.receiverId(loc.getMemberId())
				.type(NotificationType.NEARBY_POST)
				.message("근처에 새로운 제보가 올라왔어요. 골든타임이 지나기 전에 함께 찾아주세요🙏")
				.isRead(false)
				.postId(postId)
				.postType(postType)
				.build();

			notificationRepository.save(notification);
		}
	}


	/**
	 * 발견카드가 생성되고 채팅방이 열렸을 때,
	 * 분실글 작성자(=receiverId)에게 NEW_SIGHTING 알림 전송
	 */
	@Transactional
	public void notifyNewSighting(Long receiverId, Long chatRoomId, Long postId) {
		if (receiverId == null){
			throw new GeneralException(MEMBER_NOT_FOUND);
		}
		if (chatRoomId == null){
			throw new GeneralException(CHATROOM_NOT_FOUND);
		}

		Notification notification = Notification.builder()
			.receiverId(receiverId)
			.type(NotificationType.NEW_SIGHTING)
			.message("내 실종게시글에 새로운 발견카드가 도착했어요. 목격자와 1:1 채팅으로 확인해봐요.")
			.isRead(false)
			.chatRoomId(chatRoomId)
			.postId(postId)
			.postType(PostType.LOST)
			.build();

		try {
			notificationRepository.save(notification);
		} catch (DataIntegrityViolationException ignore) {
		}
	}


	/**
	 * LOST 작성자에게: 동적 메시지 + 상대(FOUND) 정보
	 */
	@Transactional
	public void notifyNewMatchForLostOwner(PostLost lost, PostFound matchedFound) {
		if (lost == null || matchedFound == null) return;

		Long receiverId = lost.getMember().getId();
		Notification noti = Notification.builder()
			.receiverId(receiverId)
			.type(NotificationType.NEW_MATCH)
			.message(buildLostOwnerMatchMessage(lost)) // "{dogName}와(과) 닮은 아이 소식이 있어요! 확인해볼까요?"
			.isRead(false)
			.postId(matchedFound.getId())
			.postType(PostType.FOUND) // 상대 타입
			.matchedPostTitle(matchedFound.getTitle())
			.build();

		try { notificationRepository.save(noti); }
		catch (DataIntegrityViolationException ignore) {}
	}

	private String buildLostOwnerMatchMessage(PostLost lost) {
		String name = lost.getDogName();
		if (name == null || name.isBlank()) {
			return "내 게시글에 매칭된 글이 있어요! 확인해볼까요?";
		}
		return "실종된 "+ name + "를 닮은 아이 소식이 있어요! 확인해볼까요?";
	}




	/**
	 * FOUND 작성자에게: 고정 메시지 + 상대(LOST) 정보
	 */
	@Transactional
	public void notifyNewMatchForFoundOwner(PostFound found, PostLost matchedLost) {
		if (found == null || matchedLost == null) return;

		Long receiverId = found.getMember().getId();
		Notification notification = Notification.builder()
			.receiverId(receiverId)
			.type(NotificationType.NEW_MATCH)
			.message("내가 발견한 강아지와 닮은 아이의 실종 신고가 등록되었어요.") // 고정문구
			.isRead(false)
			.postId(matchedLost.getId())
			.postType(PostType.LOST) // 상대 타입
			.matchedPostTitle(matchedLost.getTitle())
			.build();

		try { notificationRepository.save(notification); }
		catch (DataIntegrityViolationException ignore) {}
	}



	// 내 알림목록 조회
	@Transactional(readOnly = true)
	public List<NotificationDto.Response> getUserNotifications(Long memberId) {
		return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId)
			.stream()
			.map(NotificationConverter::toResponse)
			.collect(Collectors.toList());
	}

	// 알림 읽음처리
	@Transactional
	public void markAsRead(Long notificationId, Long memberId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new GeneralException(NOTIFICATION_NOT_FOUND));

		if (!notification.getReceiverId().equals(memberId)) {
			throw new GeneralException(CANNOT_READ_NOTIFICATION);
		}

		notification.setIsRead(true);
	}
}