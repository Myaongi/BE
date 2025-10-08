package Myaong.Gangajikimi.memberlocation.web.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.memberlocation.web.dto.MemberLocationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "MemberLocation", description = "사용자 현재 위치 관리 API")
public interface MemberLocationControllerDocs {

	@Operation(
		summary = "사용자 현재 위치 갱신",
		description = """
                로그인된 사용자의 위도, 경도 정보를 DB에 저장하거나 갱신합니다.
                
                **요청 예시(JSON)**:
                ```json
                {
                  "latitude": 37.4979,
                  "longitude": 127.0276
                }
                ```
                """,
		requestBody = @RequestBody(
			description = "사용자의 현재 위치 갱신 요청",
			required = true,
			content = @Content(
				examples = @ExampleObject(
					value = "{\"latitude\":37.4979,\"longitude\":127.0276}"
				)
			)
		),
		responses = {
			@ApiResponse(responseCode = "200", description = "위치가 성공적으로 갱신됨")
		}
	)
	ResponseEntity<GlobalResponse> updateLocation(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody MemberLocationDto.UpdateRequest req
	);
}
