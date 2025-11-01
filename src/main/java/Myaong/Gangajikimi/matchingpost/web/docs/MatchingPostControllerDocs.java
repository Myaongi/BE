package Myaong.Gangajikimi.matchingpost.web.docs;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.matchingpost.web.dto.request.MatchingPostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "매칭", description = "매칭 관련 API")
public interface MatchingPostControllerDocs {

    @Operation(
            summary = "게시글 매칭",
            description = """
                    게시글 ID를 받아 매칭 가능한 게시글들을 탐색합니다.
            
                    **작성 예시(data)**:
                    ```json
                    {
                      "postId": 0,
                      "postType": "LOST/POST"
                    }
                    ```
                    
                    """)
    @ApiResponse(responseCode = "200",
            description = "매칭 성공",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GlobalResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                                {
                                  "isSuccess": true,
                                  "code": "COMMON200",
                                  "message": "SUCCESS!",
                                  "result": {
                                    "postId": 10,
                                    "postType": "LOST",
                                    "matchingCount": 6
                                  }
                                }
                                """,
                            description = "result: MatchingResponse 객체")
            )
    )
    @ApiResponse(responseCode = "400", description = "유효하지 않은 값")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> matchingPosts(@RequestBody MatchingPostRequest request);

    @Operation(summary = "잃어버렸어요 게시글 매칭 목록 조회", description = "잃어버렸어요 게시글과 매칭된 발견했어요 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "매칭 목록 조회 성공",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GlobalResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                                {
                                    "isSuccess": true,
                                    "code": "COMMON200",
                                    "message": "SUCCESS!",
                                    "result": {
                                        "content": [
                                          {
                                            "matchingId": 8,
                                            "postId": 5,
                                            "postType": "FOUND",
                                            "title": "강아지를 주웠습니다",
                                            "dogType": "말티즈",
                                            "dogColor": "흰색",
                                            "location": "서울특별시 서초구",
                                            "similarity": 75.336494,
                                            "image": "https://gangajikimi-server...",
                                            "timeAgo": "1일 전"
                                          },
                                          {
                                            "matchingId": 9,
                                            "postId": 5,
                                            "postType": "FOUND",
                                            "title": "강아지를 주웠습니다",
                                            "dogType": "말티즈",
                                            "dogColor": "흰색",
                                            "location": "서울특별시 서초구",
                                            "similarity": 75.336494,
                                            "image": "https://gangajikimi-server...",
                                            "timeAgo": "1일 전"
                                          }
                                       ],
                                       "hastNext" : false
                                    }
                                }
                        """,
                            description = "result: MatchingResultResponse 객체")
            )
    )
    @ApiResponse(responseCode = "400", description = "유효하지 않은 값")
    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글입니다.")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> matchingPostLostList(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 사이즈", example = "20") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "잃어버렸어요 게시글 ID", in = ParameterIn.PATH) @PathVariable Long postLostId,
            @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "발견했어요 게시글 매칭 목록 조회", description = "발견했어요 게시글과 매칭 된 잃어버렸어요 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "매칭 목록 조회 성공",
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GlobalResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                                {
                                    "isSuccess": true,
                                    "code": "COMMON200",
                                    "message": "SUCCESS!",
                                    "result": {
                                        "content": [
                                          {
                                            "matchingId": 8,
                                            "postId": 5,
                                            "postType": "LOST",
                                            "title": "강아지를 주웠습니다",
                                            "dogType": "말티즈",
                                            "dogColor": "흰색",
                                            "location": "서울특별시 서초구",
                                            "similarity": 75.336494,
                                            "image": "https://gangajikimi-server...",
                                            "timeAgo": "1일 전"
                                          },
                                          {
                                            "matchingId": 9,
                                            "postId": 5,
                                            "postType": "LOST",
                                            "title": "강아지를 주웠습니다",
                                            "dogType": "말티즈",
                                            "dogColor": "흰색",
                                            "location": "서울특별시 서초구",
                                            "similarity": 75.336494,
                                            "image": "https://gangajikimi-server...",
                                            "timeAgo": "1일 전"
                                          }
                                       ],
                                       "hastNext" : false
                                    }
                                }
                                """,
                            description = "result: MatchingResultResponse 객체")
            )
    )
    @ApiResponse(responseCode = "400", description = "유효하지 않은 값")
    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글입니다.")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> matchingPostFoundList(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 사이즈", example = "20") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "발견했어요 게시글 ID", in = ParameterIn.PATH) @PathVariable Long postFoundId,
            @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "매칭 삭제", description = "매칭을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "매칭 삭제 성공")
    @ApiResponse(responseCode = "404", description = "매칭 기록이 존재하지 않습니다.")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> deleteMatching(
            @Parameter(description = "매칭 ID", in = ParameterIn.PATH) @PathVariable Long matchingId,
            @AuthenticationPrincipal CustomUserDetails userDetails);
}
