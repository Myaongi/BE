package Myaong.Gangajikimi.notification.web.dto;

import java.time.LocalDateTime;
import java.util.Map;

import Myaong.Gangajikimi.common.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

public class NotificationDto {

	@Getter
	@Builder
	public static class Response {
		private Long notificationId;
		private NotificationType type;
		private String message;
		private Boolean isRead;
		private LocalDateTime createdAt;
		private Map<String, Object> navigationTarget;
	}
}