package Myaong.Gangajikimi.matchingpost.web.docs;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.matchingpost.web.dto.request.MatchingPostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
                                    "dogName": "멍멍이",
                                    "pageResponse": {
                                      "content": [
                                        {
                                          "matchingId": 8,
                                          "postId": 5,
                                          "postType": "FOUND",
                                          "title": "강아지를 주웠습니다",
                                          "dogType": "말티즈",
                                          "dogColor": "흰색",
                                          "location": "서울특별시 서초구",
                                          "latitude": 37.4979,
                                          "longitude": 127.0276,
                                          "similarity": 75.336494,
                                          "image": "https://gangajikimi-server.s3.ap-northeast-2.amazonaws.com/postFound/36f67960-0_5.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251102T063028Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAU6CMEKYLFFSHDYQK%2F20251102%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=3600&X-Amz-Signature=7bbb5f28c95c141d35c90777c14316d0449bbaf75dc6eeb0da791c563e0d3c9f",
                                          "timeAgo": "1년 전"
                                        },
                                        {
                                          "matchingId": 17,
                                          "postId": 6,
                                          "postType": "FOUND",
                                          "title": "강아지를 주웠습니다",
                                          "dogType": "골든 리트리버",
                                          "dogColor": "갈색",
                                          "location": "서울특별시 서초구",
                                          "latitude": 37.4979,
                                          "longitude": 127.0276,
                                          "similarity": 96.79052,
                                          "image": "https://gangajikimi-server.s3.ap-northeast-2.amazonaws.com/postFound/72394364-8_6.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251102T063028Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAU6CMEKYLFFSHDYQK%2F20251102%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=3600&X-Amz-Signature=9d1ff4b1d72999bbe7ce42d7d2354e8ba310038df0a9572ccbcc12aaf268abaa",
                                          "timeAgo": "1년 전"
                                        },
                                        {
                                          "matchingId": 26,
                                          "postId": 9,
                                          "postType": "FOUND",
                                          "title": "강아지를 주웠습니다",
                                          "dogType": "말티즈",
                                          "dogColor": "흰색",
                                          "location": "서울특별시 서초구",
                                          "latitude": 37.4979,
                                          "longitude": 127.0276,
                                          "similarity": 72.967224,
                                          "image": "https://gangajikimi-server.s3.ap-northeast-2.amazonaws.com/postFound/7f1c0b83-b_9.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251102T063028Z&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAU6CMEKYLFFSHDYQK%2F20251102%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=3600&X-Amz-Signature=3eb410bfaeb27c6c71a0a114b44a40bc42a1ecd65e7061951818e0280c4d0e3b",
                                          "timeAgo": "1년 전"
                                        }
                                      ],
                                      "hasNext": false
                                    }
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
                                            "latitude": 37.4979,
                                            "longitude": 127.0276,
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
                                            "latitude": 37.4979,
                                            "longitude": 127.0276,
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

    @Operation(
            summary = "회원별 매칭된 게시글 총 개수 조회",
            description = """
                    MemberId를 입력받아 해당 Member가 작성한 게시글(잃어버렸어요 + 발견했어요)로 매칭된 게시글의 총 개수를 반환합니다.
                    
                    **동작 방식:**
                    - 해당 Member가 작성한 모든 잃어버렸어요 게시글에 대한 매칭 개수를 합산합니다.
                    - 해당 Member가 작성한 모든 발견했어요 게시글에 대한 매칭 개수를 합산합니다.
                    - 두 합계를 더한 총 개수를 반환합니다.
                    """)
    @ApiResponse(
            responseCode = "200",
            description = "매칭 개수 조회 성공",
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
                                        "memberId": 1,
                                        "totalMatchingCount": 15
                                    }
                                }
                                """,
                            description = "result: MatchingCountResponse 객체 - memberId(회원 ID), totalMatchingCount(매칭된 게시글의 총 개수)"
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "유효하지 않은 값")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> getTotalMatchingCountByMemberId(
            @Parameter(description = "회원 ID", in = ParameterIn.PATH, example = "1") @PathVariable Long memberId);

    @Operation(
            summary = "잃어버렸어요 게시글의 채팅 기록이 있는 매칭 조회 (무한 스크롤)",
            description = """
                    PostLost ID를 입력받아 해당 게시글과 매칭된 게시글 중, 채팅 기록이 존재하는 MatchingPost만 조회합니다.
                    
                    **동작 방식:**
                    - 해당 PostLost와 연결된 모든 MatchingPost를 조회합니다.
                    - 매칭된 PostFound 게시글과 채팅 기록(ChatRoom 및 ChatMessage)이 존재하는 MatchingPost만 필터링합니다.
                    - 필터링된 결과를 페이지네이션하여 반환합니다.
                    - 무한 스크롤을 지원합니다.
                    """)
    @ApiResponse(
            responseCode = "200",
            description = "매칭 조회 성공",
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
                                                "matchingId": 1,
                                                "postId": 5,
                                                "postType": "FOUND",
                                                "title": "강아지를 발견했습니다",
                                                "dogType": "말티즈",
                                                "dogColor": "흰색",
                                                "location": "서울특별시 강남구",
                                                "latitude": 37.4979,
                                                "longitude": 127.0276,
                                                "similarity": 85.5,
                                                "image": "https://...",
                                                "timeAgo": "2시간 전"
                                            }
                                        ],
                                        "hasNext": true
                                    }
                                }
                                """,
                            description = "result: PageResponse 객체 - content(매칭 결과 배열), hasNext(다음 페이지 존재 여부)"
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "유효하지 않은 값")
    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> getMatchingPostsWithChatByPostLost(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 사이즈", example = "20") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "잃어버렸어요 게시글 ID", in = ParameterIn.PATH, example = "1") @PathVariable Long postLostId,
            @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(
            summary = "발견했어요 게시글의 채팅 기록이 있는 매칭 조회 (무한 스크롤)",
            description = """
                    PostFound ID를 입력받아 해당 게시글과 매칭된 게시글 중, 채팅 기록이 존재하는 MatchingPost만 조회합니다.
                    
                    **동작 방식:**
                    - 해당 PostFound와 연결된 모든 MatchingPost를 조회합니다.
                    - 매칭된 PostLost 게시글과 채팅 기록(ChatRoom 및 ChatMessage)이 존재하는 MatchingPost만 필터링합니다.
                    - 필터링된 결과를 페이지네이션하여 반환합니다.
                    - 무한 스크롤을 지원합니다.
                    """)
    @ApiResponse(
            responseCode = "200",
            description = "매칭 조회 성공",
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
                                                "matchingId": 2,
                                                "postId": 3,
                                                "postType": "LOST",
                                                "title": "강아지를 잃어버렸습니다",
                                                "dogType": "골든 리트리버",
                                                "dogColor": "갈색",
                                                "location": "서울특별시 서초구",
                                                "latitude": 37.4837,
                                                "longitude": 127.0324,
                                                "similarity": 92.3,
                                                "image": "https://...",
                                                "timeAgo": "1일 전"
                                            }
                                        ],
                                        "hasNext": false
                                    }
                                }
                                """,
                            description = "result: PageResponse 객체 - content(매칭 결과 배열), hasNext(다음 페이지 존재 여부)"
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "유효하지 않은 값")
    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 에러")
    ResponseEntity<GlobalResponse> getMatchingPostsWithChatByPostFound(
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 사이즈", example = "20") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "발견했어요 게시글 ID", in = ParameterIn.PATH, example = "1") @PathVariable Long postFoundId,
            @AuthenticationPrincipal CustomUserDetails userDetails);
}
