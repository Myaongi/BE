package Myaong.Gangajikimi.sightcard.web.docs;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.sightcard.web.dto.SightCardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;

@Tag(name = "SightCard", description = "분실 게시글에 대한 발견카드 API")
public interface SightCardControllerDocs {

	@Operation(
		summary = "발견카드 생성",
		description = "분실 게시글에 대한 발견카드를 DB에 저장하고, 화면 표기용 데이터로 반환합니다.",
		requestBody = @RequestBody(
			description = "발견카드 생성 요청 예시(JSON)",
			required = true,
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "예시 요청",
						summary = "발견카드 생성 요청 예시",
						description = "분실 게시글 ID, 날짜, 시간, 위치(위도/경도)를 포함합니다.",
						value = """
                        {
                          "postLostId": 1,
                          "date": [2025, 10, 7],
                          "time": [2025, 10, 7, 14, 30],
                          "longitude": 127.0276,
                          "latitude": 37.4979
                        }
                        """
					)
				}
			)
		)
	)
	@ApiResponse(
		responseCode = "200",
		description = "성공적으로 생성됨",
		content = @Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(
					name = "예시 응답",
					summary = "성공 응답 예시",
					value = """
                    {
                      "isSuccess": true,
                      "code": "COMMON200",
                      "message": "SUCCESS!",
                      "result": {
                        "sightCardId": 1,
                        "postLostId": 1,
                        "postMemberId": 1,
						"foundDate": "2025.10.07",
						"foundTime": "14:30",
						"foundPlace": "서울특별시 서초구"
                        "latitude": 37.4979,
                        "longitude": 127.0276,
                      }
                    }
                    """
				)
			}
		)
	)
	ResponseEntity<GlobalResponse> createSightCard(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @org.springframework.web.bind.annotation.RequestBody SightCardDto.CreateRequest req
	);
}
