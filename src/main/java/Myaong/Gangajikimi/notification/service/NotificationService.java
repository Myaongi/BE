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
			.postId(postId)          // 있으면 저장(선택)
			.postType(PostType.LOST) // 있으면 저장(선택: NEW_SIGHTING엔 필요 없지만 통일성 위해)
			.build();

		try {
			notificationRepository.save(notification);
		} catch (DataIntegrityViolationException ignore) {
			// (선택) 유니크 인덱스에 걸리면 무시하도록
		}
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