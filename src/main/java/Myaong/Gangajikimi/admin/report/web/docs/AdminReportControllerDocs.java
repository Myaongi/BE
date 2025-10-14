// src/main/java/Myaong/Gangajikimi/admin/report/web/docs/AdminReportControllerDocs.java
package Myaong.Gangajikimi.admin.report.web.docs;

import Myaong.Gangajikimi.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AdminReport", description = "관리자: 신고 내역 관리 API")
public interface AdminReportControllerDocs {

	@Operation(summary = "신고 목록 조회")
	ResponseEntity<GlobalResponse> list(@RequestParam int page, @RequestParam int size);

	@Operation(summary = "신고 상세 조회")
	ResponseEntity<GlobalResponse> detail(@PathVariable String type, @PathVariable Long reportId);

	@Operation(summary = "신고 삭제 작업(게시글 삭제 + 신고 처리완료)")
	ResponseEntity<GlobalResponse> completeByDeletingTarget(@PathVariable String type, @PathVariable Long reportId);

	@Operation(summary = "신고 무효(신고 처리완료)")
	ResponseEntity<GlobalResponse> ignoreAndComplete(@PathVariable String type, @PathVariable Long reportId);
}
