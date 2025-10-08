package Myaong.Gangajikimi.notification.web.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notification", description = "인앱 알림 API")
public interface NotificationControllerDocs {

	@Operation(
		summary = "알림 목록 조회",
		description = "로그인된 사용자의 알림 목록을 최신순으로 반환합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "성공적으로 알림 목록 반환")
		}
	)
	ResponseEntity<GlobalResponse> getNotifications (
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "알림 읽음 처리",
		description = "특정 알림을 읽음 상태로 변경합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "성공적으로 읽음 처리됨")
		}
	)
	ResponseEntity<GlobalResponse> markAsRead(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long notificationId
	);
}