package Myaong.Gangajikimi.notification.converter;

import java.util.Map;

import Myaong.Gangajikimi.notification.entity.Notification;
import Myaong.Gangajikimi.notification.web.dto.NotificationDto;

public class NotificationConverter {

	public static NotificationDto.Response toResponse(Notification notification) {
		Map<String, Object> navTarget = switch (notification.getType()) {
			case NEARBY_POST -> Map.of(
				"screen", "PostDetail",
				"params", Map.of("postId", notification.getPostId())
			);
			case NEW_SIGHTING -> Map.of(
				"screen", "ChatDetail",
				"params", Map.of("chatroomId", notification.getChatRoomId())
			);
			case NEW_MATCH -> Map.of(
				"screen", "PostDetail",
				"params", Map.of("postId", notification.getPostId())
			);
		};

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