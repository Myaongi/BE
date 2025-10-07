package Myaong.Gangajikimi.sightcard.web.docs;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.sightcard.web.dto.SightCardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "SightCard", description = "분실 게시글에 대한 발견카드 API")
public interface SightCardControllerDocs {

	@Operation(
		summary = "발견카드 생성",
		description = """
            분실 게시글에 대한 발견카드를 DB에 저장하고, 화면 표기용 데이터로 반환합니다.
   
            **작성 예시(data)**:
            ```json
			{
			  "postLostId": 1,
			  "date": [
			    2025,
			    10,
			    7
			  ],
			  "time": [
			    2025,
			    10,
			    7,
			    14,
			    30
			  ],
			  "longitude": 127.0276,
			  "latitude": 37.4979
			}
            ```
            """
	)
	@ApiResponse(responseCode = "200", description = "성공적으로 생성됨")
	ResponseEntity<GlobalResponse> createSightCard(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody SightCardDto.CreateRequest req
	);
}
