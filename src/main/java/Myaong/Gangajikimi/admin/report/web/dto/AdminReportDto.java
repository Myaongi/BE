package Myaong.Gangajikimi.admin.report.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AdminReportDto {

	/** 목록 행 */
	@Getter
	@Setter
	@Builder @NoArgsConstructor
	@AllArgsConstructor
	public static class ListItem {
		private Long reportId;        // 신고 내역 id
		private String type;          // LOST / FOUND
		private String reason;        // 신고 사유, reportType name()
		private Long targetPostId;
		private String targetTitle;   // 신고 대상 글 제목
		private String reporterName;  // 신고자
		private LocalDateTime reportedAt;
		private String status;        // 신고 상태, reportStatus name()
	}

	/** 페이지 응답 */
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class PageResponse<T> {
		private List<T> content;
		private long totalElements;
		private int totalPages;
		private int page;
		private int size;
	}

	/** 상세 보기 */
	@Getter @Setter @Builder
	@NoArgsConstructor @AllArgsConstructor
	public static class Detail {
		private Long reportId;
		private String type;           // LOST / FOUND
		private String reason;         // reportType
		private String reporterName;
		private String reportContent;
		private LocalDateTime reportedAt;

		private Long targetPostId;
		private String targetTitle;
		private String targetContent;

		private String imagePreview;   // aiImage or first(realImage)
		private List<String> realImages; // 필요 시

		private String status;         // reportStatus
		private String detailReason;   // reportContent(자유기술)
	}

	/** 상태 변경 요청 (무시/처리완료 등) */
	@Getter @Setter
	public static class UpdateStatusRequest {
		private String status; // ReportStatus의 enum 이름(예: PENDING/RESOLVED/IGNORED 등)
	}
}