package Myaong.Gangajikimi.notification.converter;

import java.util.LinkedHashMap;
import java.util.Map;

import Myaong.Gangajikimi.common.enums.NotificationType;
import Myaong.Gangajikimi.notification.entity.Notification;
import Myaong.Gangajikimi.notification.web.dto.NotificationDto;

public class NotificationConverter {

	public static NotificationDto.Response toResponse(Notification e) {
		// params는 비어 있어도 null이면 안 됨
		Map<String, Object> params = new LinkedHashMap<>();
		String screen;

		if (e.getType() == NotificationType.NEARBY_POST) {
			screen = "PostDetail";
			if (e.getPostId() != null) params.put("postId", e.getPostId());
			if (e.getPostType() != null) params.put("postType", e.getPostType().name());

		} else if (e.getType() == NotificationType.NEW_SIGHTING) {
			screen = "ChatDetail";
			if (e.getChatRoomId() != null) params.put("chatroomId", e.getChatRoomId());

		} else { // NEW_MATCH
			screen = "PostDetail";
			if (e.getPostId() != null) params.put("postId", e.getPostId());
			if (e.getPostType() != null) params.put("postType", e.getPostType().name());
		}

		// navTarget도 안전하게 수동 조립 (Map.of 사용 안 함)
		Map<String, Object> navTarget = new LinkedHashMap<>();
		navTarget.put("screen", screen);
		navTarget.put("params", params);

		return NotificationDto.Response.builder()
			.notificationId(e.getId())
			.type(e.getType())
			.message(e.getMessage())
			.isRead(e.getIsRead())
			.createdAt(e.getCreatedAt())
			.navigationTarget(navTarget)
			.build();
	}
}