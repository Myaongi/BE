package Myaong.Gangajikimi.ai.web.docs;

import Myaong.Gangajikimi.ai.web.dto.request.DogInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "AI", description = "AI 이미지 생성 관련 API")
public interface AiControllerDocs {

    @Operation(
        summary = "AI 강아지 이미지 생성",
        description = """
            강아지 정보를 바탕으로 Gemini AI를 사용하여 강아지 이미지를 생성합니다.
            
            **요청 본문 예시:**
            ```json
            {
              "breed": "골든 리트리버",
              "colors": "골드, 흰색",
              "features": "큰 귀, 긴 꼬리, 왼쪽 귀에 반점"
            }
            ```
            
            **응답 형식:**
            - Content-Type: `image/png`
            - 응답 본문: PNG 이미지 바이너리 데이터
            - 이미지 크기: 1600px 너비, 4:3 비율
            
            **주의사항:**
            - 이미지 생성은 시간이 걸릴 수 있습니다 (일반적으로 5~15초)
            - 생성된 이미지는 PNG 형식의 바이너리 데이터입니다
            - 프론트엔드에서는 응답을 Base64로 변환하여 사용하거나, 바이너리 데이터를 직접 처리해야 합니다
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "이미지 생성 성공",
            content = @Content(
                mediaType = "image/png",
                examples = @ExampleObject(
                    description = "PNG 이미지 바이너리 데이터 (Content-Type: image/png)"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (필수 필드 누락, 잘못된 데이터 형식 등)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                            "timestamp": "2024-01-01T12:00:00",
                            "status": 400,
                            "error": "Bad Request",
                            "message": "요청 파라미터가 올바르지 않습니다",
                            "path": "/api/ai-images"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류 (FastAPI 서버 오류, 이미지 생성 실패 등)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                            "timestamp": "2024-01-01T12:00:00",
                            "status": 500,
                            "error": "Internal Server Error",
                            "message": "이미지 생성 중 오류가 발생했습니다",
                            "path": "/api/ai-images"
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<byte[]> createAiImage(
        @RequestBody(
            description = "강아지 정보 (품종, 색상, 특징)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "기본 요청 예시",
                    value = """
                        {
                            "breed": "골든 리트리버",
                            "colors": "골드, 흰색",
                            "features": "큰 귀, 긴 꼬리"
                        }
                        """,
                    description = "breed: 강아지 품종 (한국어), colors: 강아지 색상 (한국어), features: 강아지 특징 및 기타 정보 (한국어)"
                )
            )
        ) DogInfoRequest request
    );
}

