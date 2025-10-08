package Myaong.Gangajikimi.notification.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.notification.service.NotificationService;
import Myaong.Gangajikimi.notification.web.docs.NotificationControllerDocs;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationControllerDocs {

	private final NotificationService notificationService;

	@GetMapping
	@Override
	public ResponseEntity<GlobalResponse> getNotifications(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		Long memberId = userDetails.getId();
		var response = notificationService.getUserNotifications(memberId);
		return GlobalResponse.onSuccess(SuccessCode.OK, response);
	}

	@PatchMapping("/{notificationId}/read")
	@Override
	public ResponseEntity<GlobalResponse> markAsRead(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long notificationId
	) {
		notificationService.markAsRead(notificationId, userDetails.getId());
		return GlobalResponse.onSuccess(SuccessCode.OK, "알림이 읽음 처리되었습니다.");
	}
}
