package Myaong.Gangajikimi.postfound.web.docs;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundDogStatusUpdateRequest;
import Myaong.Gangajikimi.postfoundreport.dto.PostFoundReportRequest;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PostFound", description = "습득물 게시글 관련 API")
public interface PostFoundControllerDocs {

    @Operation(
        summary = "발견했어요 게시글 작성",
        description = """
            Multipart/form-data 형식으로 data(JSON)와 images(이미지 파일), ai image를 전송합니다.
            
            
            **작성 예시(data)**:
            ```json
            {
              "title": "강아지를 주웠습니다",
              "dogType": "말티즈",
              "dogColor": "흰색",
              "dogGender": "MALE",
              "features": "목걸이가 있었습니다",
              "foundDate": [2024, 1, 1],
              "foundTime": [2024, 1, 1, 14, 30, 0, 0],
              "foundLongitude": 127.0276,
              "foundLatitude": 37.4979
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
                                "postTitle": "강아지를 주웠습니다",
                                "postDate": [2024, 1, 1, 12, 0, 0, 0],
                                "dogStatus": "MISSING"
                            }
                        }
                        """,
                    description = "result: PostFoundResponse 객체 - postId(게시글 ID), memberName(작성자명), postTitle(제목), postDate(작성일시), dogStatus(강아지 상태: MISSING/SIGHTED/RETURNED)"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (필수 필드 누락, 잘못된 데이터 형식 등)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "VALIDATION_ERROR",
                            "message": "입력값이 올바르지 않습니다",
                            "result": null
                        }
                        """
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
            description = "견종을 찾을 수 없음",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "DOG_TYPE_NOT_FOUND",
                            "message": "존재하지 않는 견종입니다",
                            "result": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (필수 필드 누락, 잘못된 데이터 형식 등)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "VALIDATION_ERROR",
                            "message": "입력값이 올바르지 않습니다",
                            "result": null
                        }
                        """
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
            description = "견종을 찾을 수 없음",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "DOG_TYPE_NOT_FOUND",
                            "message": "존재하지 않는 견종입니다",
                            "result": null
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> postFound(
        String dataJson,
        java.util.List<org.springframework.web.multipart.MultipartFile> images,
        org.springframework.web.multipart.MultipartFile aiImage,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws com.fasterxml.jackson.core.JsonProcessingException;

    @Operation(
        summary = "습득물 게시글 수정",
        description = """
            기존 습득물 게시글을 수정합니다. 본인이 작성한 게시글만 수정할 수 있습니다.
            
            **이미지 수정 기능:**
            - existingImageUrls: 유지할 기존 이미지 URL들
            - deletedImageUrls: 삭제할 이미지 URL들
            - images: 새로 추가할 이미지 파일들
            
            **작성 예시(data)**:
            ```json
            {
              "title": "강아지를 주웠습니다",
              "dogType": "말티즈",
              "dogColor": "흰색",
              "dogGender": "MALE",
              "features": "목걸이가 있었습니다",
              "foundDate": [2024, 1, 1],
              "foundTime": [2024, 1, 1, 14, 30, 0, 0],
              "foundLongitude": 127.0276,
              "foundLatitude": 37.4979,
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
                    description = "result: PostFoundResponse 객체 - postId(게시글 ID), memberName(작성자명), postTitle(수정된 제목), postDate(수정일시), dogStatus(강아지 상태: MISSING/SIGHTED/RETURNED)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> updateFound(
        String dataJson,
        java.util.List<org.springframework.web.multipart.MultipartFile> images,
        org.springframework.web.multipart.MultipartFile aiImage,
        @PathVariable Long postFoundId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws com.fasterxml.jackson.core.JsonProcessingException;

    @Operation(
        summary = "습득물 게시글 삭제",
        description = "습득물 게시글을 삭제합니다. 본인이 작성한 게시글만 삭제할 수 있습니다."
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
    ResponseEntity<GlobalResponse> deleteFound(
        @PathVariable Long postFoundId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
        summary = "습득물 게시글 상세 조회",
        description = "특정 습득물 게시글의 상세 정보를 조회합니다."
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
                                "title": "강아지를 주웠습니다",
                                "dogType": "말티즈",
                                "dogColor": "흰색",
                                "dogGender": "MALE",
                                "dogStatus": "MISSING",
                                "content": "어제 공원에서 강아지를 주웠습니다...",
                                "foundDate": [2024, 1, 1],
                                "foundTime": [2024, 1, 1, 14, 30, 0, 0],
                                "longitude": 127.0276,
                                "latitude": 37.4979,
                                "foundRegion": "서울시 서초구",
                                "aiImage" : "https://s3.amazonaws.com/bucket/presigned-url-example1",
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
                    description = "result: PostFoundDetailResponse 객체 - postId(게시글 ID), title(제목), dogType(견종), dogColor(색상), dogGender(성별: MALE/FEMALE), dogStatus(강아지 상태: MISSING/SIGHTED/RETURNED), content(내용), foundDate(습득 날짜), foundTime(습득 시간), longitude(경도), latitude(위도), foundRegion(행정구역), realImages(실제 이미지 Presigned URL 목록), authorId(작성자 ID), authorName(작성자명), createdAt(작성일시), timeAgo(상대시간)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> getPostFoundDetail(@PathVariable Long postFoundId);

    @Operation(
        summary = "습득물 게시글 신고",
        description = "부적절한 습득물 게시글을 신고합니다. 본인의 게시글은 신고할 수 없습니다."
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
                    description = "result: PostFoundReportResponse 객체 - postId(게시글 ID), createdAt(신고일시)"
                )
            )
        )
    })
    ResponseEntity<GlobalResponse> reportPostFound(
        @PathVariable Long postFoundId,
        @RequestBody PostFoundReportRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
        summary = "목격했어요 게시글 목록 조회 (필터링)",
        description = """
            목격했어요 게시글 목록을 페이지네이션 및 필터링으로 조회합니다.
            
            **Query String 파라미터 예시:**
            ```
            GET /api/found-posts?page=0&size=20&sortType=LATEST&maxDistance=3&timeFilter=ONE_WEEK&userLongitude=127.0276&userLatitude=37.4979
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
                                        "title": "강아지를 목격했습니다",
                                        "dogType": "말티즈",
                                        "dogColor": "흰색",
                                        "location": "서울시 서초구",
                                        "foundDateTime": [2024, 1, 1, 14, 30, 0, 0],
                                        "image": "https://s3.amazonaws.com/bucket/presigned-url-example",
                                        "status": "MISSING"
                                    },
                                    {
                                        "id": 2,
                                        "title": "골든 리트리버를 목격했습니다",
                                        "dogType": "골든 리트리버",
                                        "dogColor": "갈색",
                                        "location": "서울시 서초구",
                                        "foundDateTime": [2024, 1, 1, 13, 0, 0, 0],
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
    ResponseEntity<GlobalResponse> getFoundPosts(
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
        summary = "내 목격했어요 게시글 조회",
        description = "특정 사용자가 작성한 목격했어요 게시글 목록을 페이지네이션으로 조회합니다."
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
                                        "title": "강아지를 목격했습니다",
                                        "dogType": "MALTESE",
                                        "dogColor": "흰색",
                                        "location": "서울시 서초구",
                                        "lostDateTime": [2024, 1, 1, 14, 30, 0, 0],
                                        "image": "https://example.com/image1.jpg",
                                        "type": "FOUND",
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
    ResponseEntity<GlobalResponse> getMyFoundPosts(
        @io.swagger.v3.oas.annotations.Parameter(hidden = true) CustomUserDetails userDetails,
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
        summary = "습득물 게시글 강아지 상태 일괄 업데이트",
        description = """
            여러 습득물 게시글의 강아지 상태를 한 번에 업데이트합니다.
            
            **동작 방식:**
            - 요청 본문에 게시글 ID 목록과 변경할 상태를 전송하면, 해당 게시글들의 상태가 일괄 업데이트됩니다.
            - 본인이 작성한 게시글만 상태를 변경할 수 있습니다. (관리자 제외)
            - 하나라도 권한이 없는 게시글이 포함되어 있으면 전체 요청이 실패합니다.
            
            **요청 예시:**
            ```json
            {
              "postFoundIds": [1, 2, 3],
              "dogStatus": "RETURNED"
            }
            ```
            
            **가능한 상태값:**
            - MISSING: 실종
            - SIGHTED: 목격  
            - RETURNED: 귀가완료
            
            **주의사항:**
            - postFoundIds는 빈 배열일 수 없습니다.
            - 모든 게시글 ID는 유효해야 하며, 본인의 게시글이어야 합니다.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "강아지 상태 일괄 업데이트 성공",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GlobalResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": true,
                            "code": "COMMON200",
                            "message": "SUCCESS!",
                            "result": [
                                {
                                    "postId": 1,
                                    "dogStatus": "RETURNED",
                                    "updatedAt": [2024, 1, 1, 15, 30, 0, 0]
                                },
                                {
                                    "postId": 2,
                                    "dogStatus": "RETURNED",
                                    "updatedAt": [2024, 1, 1, 15, 30, 0, 0]
                                },
                                {
                                    "postId": 3,
                                    "dogStatus": "RETURNED",
                                    "updatedAt": [2024, 1, 1, 15, 30, 0, 0]
                                }
                            ]
                        }
                        """,
                    description = "result: DogStatusUpdateResponse 배열 - 각 게시글별 업데이트 결과 (postId, dogStatus, updatedAt)"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (게시글 ID 목록이 비어있거나, dogStatus가 유효하지 않음)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "VALIDATION_FAILED",
                            "message": "유효하지 않은 요청입니다",
                            "result": null
                        }
                        """
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
            responseCode = "403",
            description = "권한 없음 (본인의 게시글이 아님)",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = """
                        {
                            "isSuccess": false,
                            "code": "UNAUTHORIZED_UPDATING",
                            "message": "수정 권한이 없습니다",
                            "result": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음 (요청한 게시글 ID 중 하나라도 존재하지 않음)",
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
    ResponseEntity<GlobalResponse> updatePostFoundStatus(
        @RequestBody PostFoundDogStatusUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
