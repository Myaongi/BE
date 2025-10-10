package Myaong.Gangajikimi.admin.member.web.controller;

import java.util.List;

import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.admin.member.service.AdminMemberService;
import Myaong.Gangajikimi.admin.member.web.docs.AdminMemberControllerDocs;
import Myaong.Gangajikimi.admin.member.web.dto.AdminMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 - 회원 관리 컨트롤러
 * 관리자 권한(ROLE_ADMIN)만 접근 가능
 */
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController implements AdminMemberControllerDocs {

	private final AdminMemberService service;

	/**
	 * 회원 목록 조회 (검색 + 페이지네이션 + 총 사용자 수)
	 */
	@GetMapping
	@Override
	public ResponseEntity<GlobalResponse> list(
		@RequestParam(required = false) String query,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		var result = service.getMembers(query, PageRequest.of(page, size));
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}

	/**
	 * 회원 상세 조회 (헤더 정보 + 활동 요약)
	 */
	@GetMapping("/{memberId}")
	@Override
	public ResponseEntity<GlobalResponse> detail(@PathVariable Long memberId) {
		var result = service.getDetail(memberId);
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}

	/**
	 * 회원 활동 로그 - 작성글 목록
	 * type = LOST / FOUND (null이면 둘 다)
	 */
	@GetMapping("/{memberId}/posts")
	@Override
	public ResponseEntity<GlobalResponse> posts(
		@PathVariable Long memberId,
		@RequestParam(required = false) String type
	) {
		List<AdminMemberDto.PostSummary> result = service.getPostsByMember(memberId, type);
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}

	/**
	 * 회원 활동 로그 - 신고 내역 (해당 사용자가 제출한 신고)
	 */
	@GetMapping("/{memberId}/reports")
	@Override
	public ResponseEntity<GlobalResponse> reports(@PathVariable Long memberId) {
		List<AdminMemberDto.ReportSummary> result = service.getReportsByMember(memberId);
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}

	/**
	 * 사용자 계정 상태 변경 (정지 / 복구)
	 */
	@PatchMapping("/{memberId}/status")
	@Override
	public ResponseEntity<GlobalResponse> updateStatus(
		@PathVariable Long memberId,
		@RequestBody AdminMemberDto.UpdateStatusRequest req
	) {
		service.updateStatus(memberId, req.getStatus());
		return GlobalResponse.onSuccess(SuccessCode.OK, "계정 상태가 변경되었습니다.");
	}

	/**
	 * 회원 삭제 (Hard Delete)
	 */
	@DeleteMapping("/{memberId}")
	@Override
	public ResponseEntity<GlobalResponse> delete(@PathVariable Long memberId) {
		service.deleteHard(memberId);
		return GlobalResponse.onSuccess(SuccessCode.OK, "계정 삭제가 완료되었습니다.");
	}
}
