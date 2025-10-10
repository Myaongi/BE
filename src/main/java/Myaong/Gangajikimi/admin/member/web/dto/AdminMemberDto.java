package Myaong.Gangajikimi.admin.member.web.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class AdminMemberDto {

	// 목록 행
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class ListItem {
		private Long id;
		private String nickname;
		private String email;
		private LocalDateTime joinedAt;
		private String status; // ACTIVATED / LOCKED / UNACTIVATED
	}

	// 페이지 응답 (상단 총 사용자 수 포함)
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class PageResponse<T> {
		private List<T> content;
		private long totalElements;
		private int totalPages;
		private int page;
		private int size;
		private long totalUsers; // 상단 "총 사용자 수"
	}

	// 상세 + 활동 요약(모달 헤더 + 하이라이트)
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class Detail {
		private Long id;
		private String nickname;
		private String email;
		private LocalDateTime joinedAt;
		private String status;
		private ActivitySummary activity;
	}

	// 활동 요약(카운트 + 최근 항목)
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class ActivitySummary {
		private int lostCount; // 실종 게시글 수
		private int foundCount; // 목격 게시글 수
		private int postAllCount; // 사용자의 전체 게시글 수
		private int reportCount; // 사용자가 제출한 신고 수
		private List<PostSummary> postsByMember;
		private List<ReportSummary> reportsByMember;
	}

	// 탭: 작성글 목록 카드
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class PostSummary {
		private Long postId; // 게시글 id
		private String type; // LOST / FOUND
		private String title; // 게시글 제목
		private String region; // 구/군 등 (엔티티에 맞춰 조정)
		private LocalDateTime createdAt; // 게시글 작성일
		private String thumbnailUrl; // 선택
	}

	// 탭: 신고 내역 카드
	@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
	public static class ReportSummary {
		private Long reportId;
		private String targetType;   // LOST / FOUND
		private Long targetPostId;
		private String targetTitle;
		private String reportType;     // 신고 분류
		private String reportContent;  // 신고사유
		private String reportStatus;       // 처리 여부
		private LocalDateTime reportedAt;  // 신고 날짜
	}

	// 정지/복구
	@Getter @Setter
	public static class UpdateStatusRequest {
		private String status; // 허용값: ACTIVATED / LOCKED / UNACTIVATED
	}
}
