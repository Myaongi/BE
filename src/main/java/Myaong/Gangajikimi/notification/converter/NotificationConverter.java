package Myaong.Gangajikimi.notification.converter;

import java.util.LinkedHashMap;
import java.util.Map;

import Myaong.Gangajikimi.common.enums.NotificationType;
import Myaong.Gangajikimi.notification.entity.Notification;
import Myaong.Gangajikimi.notification.web.dto.NotificationDto;

public class NotificationConverter {

	public static NotificationDto.Response toResponse(Notification notification) {
		// params는 비어 있어도 null이면 안 됨
		Map<String, Object> params = new LinkedHashMap<>();
		String screen;

		if (notification.getType() == NotificationType.NEARBY_POST) {
			screen = "PostDetail";
			if (notification.getPostId() != null) params.put("postId", notification.getPostId());
			if (notification.getPostType() != null) params.put("postType", notification.getPostType().name());

		} else if (notification.getType() == NotificationType.NEW_SIGHTING) {
			screen = "ChatDetail";
			if (notification.getChatRoomId() != null) params.put("chatroomId", notification.getChatRoomId());

		} else { // NEW_MATCH
			screen = "PostDetail";
			if (notification.getPostId() != null) params.put("postId", notification.getPostId());
			if (notification.getPostType() != null) params.put("postType", notification.getPostType().name());
			if (notification.getMatchedPostTitle() != null) params.put("matchedPostTitle", notification.getMatchedPostTitle());
		}

		// navTarget도 안전하게 수동 조립 (Map.of 사용 안 함)
		Map<String, Object> navTarget = new LinkedHashMap<>();
		navTarget.put("screen", screen);
		navTarget.put("params", params);

		return NotificationDto.Response.builder()
			.notificationId(notification.getId())
			.type(notification.getType())
			.message(notification.getMessage())
			.isRead(notification.getIsRead())
			.createdAt(notification.getCreatedAt())
			.navigationTarget(navTarget)
			.build();
	}
}