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
import org.springframework.web.bind.annotation.PathVariable;

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
						"result": {
						     "sightCard": {
						       "sightCardId": 41,
						       "postLostId": 16,
						       "reporterId": 7,
						       "foundDate": "2025.09.18",
						       "foundTime": "03:56",
						       "foundPlace": "문정동 로데오앞",
						       "longitude": 127.12203,
						       "latitude": 37.48421
						     },
						     "chatRoom": {
						       "chatroomId": 3,
						       "member1Id": 2,
						       "member2Id": 7,
						       "createdAt": "2025-10-07T22:44:31.509185"
						     }
						   }
                    """
				)
			}
		)
	)
	ResponseEntity<GlobalResponse> createSightCardWithChat(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @org.springframework.web.bind.annotation.RequestBody SightCardDto.CreateRequest req
	);

	@Operation(
		summary = "채팅방의 발견카드 조회",
		description = "해당 채팅방에 연결된 발견카드를 반환합니다."
	)
		// 필요하면 ApiResponse 예시도 추가 가능
	ResponseEntity<GlobalResponse> getSightCardByChatRoom(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable("chatRoomId") Long chatRoomId
	);
}

