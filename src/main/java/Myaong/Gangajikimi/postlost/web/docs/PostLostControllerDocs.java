package Myaong.Gangajikimi.postlost.web.docs;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.dto.request.DogStatusUpdateRequest;
import Myaong.Gangajikimi.postlostreport.dto.PostLostReportRequest;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PostLost", description = "분실물 게시글 관련 API")
public interface PostLostControllerDocs {

    @Operation(
        summary = "분실물 게시글 작성",
        description = """
            Multipart/form-data 형식으로 data(JSON)와 images(이미지 파일)를 전송합니다.
            
            **작성 예시(data)**:
            ```json
            {
              "title": "강아지를 잃어버렸습니다",
              "dogName": "멍멍이",
              "dogType": "골든 리트리버",
              "dogColor": "갈색",
              "dogGender": "FEMALE",
              "features": "귀여운 목걸이",
              "lostDate": [2024, 1, 1],
              "lostTime": [2024, 1, 1, 14, 30, 0, 0],
              "lostLongitude": 127.0276,
              "lostLatitude": 37.4979
            }
            ```
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 작성 성공",
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
                                "postId": 1,
                                "memberName": "홍길동",
                                "postTitle": "강아지를 잃어버렸습니다",
                                "postDate": [2024, 1, 1, 12, 0, 0, 0],
                                "dogStatus": "MISSING"
                            }
                        }
                        """,
                    description = "result: PostLostPostResponse 객체 - postId(게시글 ID), memberName(작성자명), postTitle(제목), postDate(작성일시), dogStatus(강아지 상태: MISSING/SIGHTED/RETURNED)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> postLost(
        String dataJson,
        java.util.List<org.springframework.web.multipart.MultipartFile> images,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws com.fasterxml.jackson.core.JsonProcessingException;

    @Operation(
        summary = "분실물 게시글 수정",
        description = """
            기존 분실물 게시글을 수정합니다. 본인이 작성한 게시글만 수정할 수 있습니다.
            
            **이미지 수정 기능:**
            - existingImageUrls: 유지할 기존 이미지 URL들
            - deletedImageUrls: 삭제할 이미지 URL들
            - images: 새로 추가할 이미지 파일들
            
            **작성 예시(data)**:
            ```json
            {
              "title": "강아지를 잃어버렸습니다",
              "dogName": "멍멍이",
              "dogType": "골든 리트리버",
              "dogColor": "갈색",
              "dogGender": "FEMALE",
              "features": "귀여운 목걸이",
              "lostDate": [2024, 1, 1],
              "lostTime": [2024, 1, 1, 14, 30, 0, 0],
              "lostLongitude": 127.0276,
              "lostLatitude": 37.4979,
              "existingImageUrls": ["https://s3.amazonaws.com/bucket/presigned-url1"],
              "deletedImageUrls": ["https://s3.amazonaws.com/bucket/presigned-url2"]
            }
            ```
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 수정 성공",
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
                                "postId": 1,
                                "memberName": "홍길동",
                                "postTitle": "수정된 제목",
                                "postDate": [2024, 1, 1, 13, 0, 0, 0],
                                "dogStatus": "MISSING"
                            }
                        }
                        """,
                    description = "result: PostLostPostResponse 객체 - postId(게시글 ID), memberName(작성자명), postTitle(수정된 제목), postDate(수정일시), dogStatus(강아지 상태: MISSING/SIGHTED/RETURNED)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> updateLost(
        String dataJson,
        java.util.List<org.springframework.web.multipart.MultipartFile> images,
        @PathVariable Long postLostId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws com.fasterxml.jackson.core.JsonProcessingException;

    @Operation(
        summary = "분실물 게시글 삭제",
        description = "분실물 게시글을 삭제합니다. 본인이 작성한 게시글만 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 삭제 성공",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GlobalResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": true,
                            "code": "COMMON200",
                            "message": "SUCCESS!",
                            "result": null
                        }
                        """,
                    description = "result: null - 게시글 삭제 성공 시 별도 데이터 없음"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> deleteLost(
        @PathVariable Long postLostId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
        summary = "분실물 게시글 상세 조회",
        description = "특정 분실물 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 조회 성공",
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
                                "postId": 1,
                                "title": "강아지를 잃어버렸습니다",
                                "dogName": "멍멍이",
                                "dogType": "말티즈",
                                "dogColor": "흰색",
                                "dogGender": "MALE",
                                "dogStatus": "MISSING",
                                "content": "어제 공원에서 강아지를 잃어버렸습니다...",
                                "lostDate": [2024, 1, 1],
                                "lostTime": [2024, 1, 1, 14, 30, 0, 0],
                                "longitude": 127.0276,
                                "latitude": 37.4979,
                                "lostRegion": "서울시 서초구",
                                "realImages": [
                                    "https://s3.amazonaws.com/bucket/presigned-url-example1",
                                    "https://s3.amazonaws.com/bucket/presigned-url-example2"
                                ],
                                "authorId": 1,
                                "authorName": "홍길동",
                                "createdAt": [2024, 1, 1, 12, 0, 0, 0],
                                "timeAgo": "2시간 전"
                            }
                        }
                        """,
                    description = "result: PostLostDetailResponse 객체 - postId(게시글 ID), title(제목), dogName(강아지 이름), dogType(견종), dogColor(색상), dogGender(성별: MALE/FEMALE), dogStatus(강아지 상태: MISSING/SIGHTED/RETURNED), content(내용), lostDate(분실 날짜), lostTime(분실 시간), longitude(경도), latitude(위도), lostRegion(행정구역), realImages(실제 이미지 Presigned URL 목록), authorId(작성자 ID), authorName(작성자명), createdAt(작성일시), timeAgo(상대시간)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> getPostLostDetail(@PathVariable Long postLostId);

    @Operation(
        summary = "분실물 게시글 신고",
        description = "부적절한 분실물 게시글을 신고합니다. 본인의 게시글은 신고할 수 없습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "신고 성공",
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
                                "postId": 1,
                                "createdAt": "2024-01-01T12:00:00"
                            }
                        }
                        """,
                    description = "result: PostLostReportResponse 객체 - postId(게시글 ID), createdAt(신고일시)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> reportPostLost(
        @PathVariable Long postLostId,
        @RequestBody PostLostReportRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
        summary = "잃어버렸어요 게시글 목록 조회 (필터링)",
        description = """
            잃어버렸어요 게시글 목록을 페이지네이션 및 필터링으로 조회합니다.
            
            **Query String 파라미터 예시:**
            ```
            GET /api/lost-posts?page=0&size=20&sortType=LATEST&maxDistance=3&timeFilter=ONE_WEEK&userLongitude=127.0276&userLatitude=37.4979
            ```
            
            **필터 옵션 (모두 선택사항):**
            - sortType: 정렬 기준 (LATEST: 최신순, DISTANCE: 거리순) - 기본값: LATEST
            - maxDistance: 최대 거리 (1~5km, 미입력 시 전체)
            - timeFilter: 시간 필터 (ONE_HOUR, ONE_DAY, ONE_WEEK, ONE_MONTH, 미입력 시 전체)
            - userLongitude: 사용자 경도 (거리순 정렬 시 필수)
            - userLatitude: 사용자 위도 (거리순 정렬 시 필수)
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "게시글 목록 조회 성공",
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
                                        "id": 1,
                                        "title": "강아지를 잃어버렸습니다",
                                        "dogType": "말티즈",
                                        "dogColor": "흰색",
                                        "location": "서울시 서초구",
                                        "lostDateTime": [2024, 1, 1, 14, 30, 0, 0],
                                        "image": "https://s3.amazonaws.com/bucket/presigned-url-example",
                                        "status": "MISSING"
                                    },
                                    {
                                        "id": 2,
                                        "title": "골든 리트리버를 잃어버렸습니다",
                                        "dogType": "골든 리트리버",
                                        "dogColor": "갈색",
                                        "location": "서울시 서초구",
                                        "lostDateTime": [2024, 1, 1, 13, 0, 0, 0],
                                        "image": "https://s3.amazonaws.com/bucket/presigned-url-example2",
                                        "status": "SIGHTED"
                                    }
                                ],
                                "hasNext": true
                            }
                        }
                        """,
                    description = "result: PageResponse 객체 - posts(HomePostResponse 배열), hasNext(다음 페이지 존재 여부)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> getLostPosts(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "페이지 번호 (0부터 시작)",
            example = "0"
        ) Integer page,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "페이지 크기",
            example = "20"
        ) Integer size,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "정렬 기준 (LATEST: 최신순, DISTANCE: 거리순)",
            example = "LATEST"
        ) Myaong.Gangajikimi.common.enums.SortType sortType,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "최대 거리 (1~5km)",
            example = "3"
        ) Integer maxDistance,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "시간 필터 (ONE_HOUR, ONE_DAY, ONE_WEEK, ONE_MONTH)",
            example = "ONE_WEEK"
        ) Myaong.Gangajikimi.common.enums.TimeFilter timeFilter,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "사용자 경도",
            example = "127.0276"
        ) Double userLongitude,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "사용자 위도",
            example = "37.4979"
        ) Double userLatitude
    );

    @Operation(
        summary = "내 잃어버렸어요 게시글 조회",
        description = "로그인한 사용자가 작성한 잃어버렸어요 게시글 목록을 페이지네이션으로 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "내 게시글 목록 조회 성공",
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
                                "posts": [
                                    {
                                        "id": 1,
                                        "title": "강아지를 잃어버렸습니다",
                                        "dogType": "MALTESE",
                                        "dogColor": "흰색",
                                        "location": "서울시 서초구",
                                        "lostDateTime": [2024, 1, 1, 14, 30, 0, 0],
                                        "image": "https://example.com/image1.jpg",
                                        "type": "LOST",
                                        "status": "상태"
                                    }
                                ],
                                "hasNext": false
                            }
                        }
                        """,
                    description = "result: PageResponse 객체 - posts(HomePostResponse 배열), hasNext(다음 페이지 존재 여부)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> getMyLostPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "페이지 번호 (0부터 시작)",
            example = "0"
        ) Integer page,
        @io.swagger.v3.oas.annotations.Parameter(
            description = "페이지 크기",
            example = "20"
        ) Integer size
    );

    @Operation(
        summary = "분실물 게시글 강아지 상태 업데이트",
        description = """
            분실물 게시글의 강아지 상태만 업데이트합니다.
            
            **요청 예시:**
            ```json
            {
              "dogStatus": "RETURNED"
            }
            ```
            
            **가능한 상태값:**
            - MISSING: 실종
            - SIGHTED: 목격  
            - RETURNED: 귀가완료
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "강아지 상태 업데이트 성공",
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
                                "postId": 1,
                                "dogStatus": "RETURNED",
                                "updatedAt": [2024, 1, 1, 15, 30, 0, 0]
                            }
                        }
                        """,
                    description = "result: DogStatusUpdateResponse 객체 - postId(게시글 ID), dogStatus(업데이트된 강아지 상태), updatedAt(업데이트 시간)"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 (토큰이 없거나 유효하지 않음)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "UNAUTHORIZED",
                            "message": "인증이 필요합니다",
                            "result": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "POST_NOT_FOUND",
                            "message": "게시글을 찾을 수 없습니다",
                            "result": null
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> updatePostLostStatus(
        @PathVariable Long postLostId,
        @RequestBody DogStatusUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
