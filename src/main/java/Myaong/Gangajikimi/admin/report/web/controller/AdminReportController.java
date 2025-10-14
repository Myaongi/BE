package Myaong.Gangajikimi.admin.report.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Myaong.Gangajikimi.admin.report.service.AdminReportService;
import Myaong.Gangajikimi.admin.report.web.docs.AdminReportControllerDocs;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController implements AdminReportControllerDocs {

	private final AdminReportService service;

	@GetMapping
	@Override
	public ResponseEntity<GlobalResponse> list(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		var result = service.list(page, size);
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}

	@GetMapping("/{type}/{reportId}")
	@Override
	public ResponseEntity<GlobalResponse> detail(
		@PathVariable String type,
		@PathVariable Long reportId
	) {
		var result = service.detail(type, reportId);
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}

	/** 신고의 '삭제' 작업: 대상 게시글 삭제 + 신고 상태 COMPLETED */
	@DeleteMapping("/{type}/{reportId}/delete")
	@Override
	public ResponseEntity<GlobalResponse> completeByDeletingTarget(
		@PathVariable String type,
		@PathVariable Long reportId
	) {
		service.completeByDeletingTarget(type, reportId);
		return GlobalResponse.onSuccess(SuccessCode.OK, "신고된 게시물을 삭제하였습니다.");
	}

	/** 신고의 '무시' 작업: 상태만 COMPLETED */
	@PatchMapping("/{type}/{reportId}/ignore")
	@Override
	public ResponseEntity<GlobalResponse> ignoreAndComplete(
		@PathVariable String type,
		@PathVariable Long reportId
	) {
		service.ignoreAndComplete(type, reportId);
		return GlobalResponse.onSuccess(SuccessCode.OK, "해당 신고는 무효처리되었습니다.");
	}
}