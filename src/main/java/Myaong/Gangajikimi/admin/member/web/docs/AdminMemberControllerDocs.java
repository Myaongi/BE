package Myaong.Gangajikimi.admin.member.web.docs;

import java.util.List;

import Myaong.Gangajikimi.admin.member.web.dto.AdminMemberDto;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 - 사용자 관리 API 문서
 */
@Tag(name = "AdminMember", description = "관리자: 사용자 관리 API")
public interface AdminMemberControllerDocs {

	@Operation(
		summary = "사용자 목록 조회",
		description = """
			닉네임 또는 이메일로 사용자 검색 가능하며,
			페이지네이션을 지원합니다.
			응답에는 총 사용자 수 및 목록 데이터가 포함됩니다.
			"""
	)
	@ApiResponse(
		responseCode = "200",
		description = "성공",
		content = @Content(examples = @ExampleObject(value = """
			{
			   {
			     "isSuccess": true,
			     "code": "COMMON200",
			     "message": "SUCCESS!",
			     "result": {
			       "content": [
			         {
			           "id": 1,
			           "nickname": "lee",
			           "email": "lee@naver.com",
			           "joinedAt": [
			             2025,
			             10,
			             10,
			             15,
			             35,
			             13,
			             497559000
			           ],
			           "status": "ACTIVATED"
			         },
			         {
			           "id": 3,
			           "nickname": "lee3",
			           "email": "lee3@naver.com",
			           "joinedAt": [
			             2025,
			             10,
			             10,
			             19,
			             36,
			             33,
			             267662000
			           ],
			           "status": "ACTIVATED"
			         }
			       ],
			       "totalElements": 2,
			       "totalPages": 1,
			       "page": 0,
			       "size": 20,
			       "totalUsers": 2
			     }
			   }
			"""))
	)
	ResponseEntity<GlobalResponse> list(
		@RequestParam(required = false) String query,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	);

	@Operation(
		summary = "사용자 상세 조회",
		description = "선택한 사용자의 기본정보와 최근 활동(작성글, 신고 내역 요약)을 조회합니다."
	)
	@ApiResponse(responseCode = "200", description = "성공")
	ResponseEntity<GlobalResponse> detail(@PathVariable Long memberId);

	@Operation(
		summary = "작성글 목록 조회",
		description = "해당 사용자가 작성한 분실글 또는 목격글을 조회합니다. type = LOST / FOUND (null이면 전체)"
	)
	@ApiResponse(responseCode = "200", description = "성공")
	ResponseEntity<GlobalResponse> posts(
		@PathVariable Long memberId,
		@RequestParam(required = false) String type
	);

	@Operation(
		summary = "신고 내역 조회",
		description = "해당 사용자가 제출한 신고 내역을 조회합니다."
	)
	@ApiResponse(responseCode = "200", description = "성공")
	ResponseEntity<GlobalResponse> reports(@PathVariable Long memberId);

	@Operation(
		summary = "계정 상태 변경",
		description = "관리자가 특정 사용자의 계정을 정지(LOCKED) 또는 복구(ACTIVATED)합니다.",
		requestBody = @RequestBody(
			description = "계정 상태 변경 요청 예시",
			required = true,
			content = @Content(examples = @ExampleObject(value = """
				{
				  "status": "LOCKED"
				}
				"""))
		)
	)
	@ApiResponse(responseCode = "200", description = "성공")
	ResponseEntity<GlobalResponse> updateStatus(
		@PathVariable Long memberId,
		@Valid @RequestBody AdminMemberDto.UpdateStatusRequest req
	);

	@Operation(
		summary = "계정 삭제",
		description = "관리자가 특정 사용자를 영구 삭제(Hard Delete)합니다."
	)
	@ApiResponse(responseCode = "200", description = "성공")
	ResponseEntity<GlobalResponse> delete(@PathVariable Long memberId);
}
