package Myaong.Gangajikimi.admin.member.service;

import java.util.ArrayList;
import java.util.List;

import Myaong.Gangajikimi.s3file.service.S3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Myaong.Gangajikimi.admin.member.web.dto.AdminMemberDto;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.member.repository.MemberRepository;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport;
import Myaong.Gangajikimi.postfoundreport.repository.PostFoundReportRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.postlostreport.entity.PostLostReport;
import Myaong.Gangajikimi.postlostreport.repository.PostLostReportRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

	private final MemberRepository memberRepo;
	private final PostLostRepository lostRepo;
	private final PostFoundRepository foundRepo;
	private final PostLostReportRepository lostReportRepo;
	private final PostFoundReportRepository foundReportRepo;
    private final S3Service s3Service;

	// 전체 사용자 목록
	public AdminMemberDto.PageResponse<AdminMemberDto.ListItem> getMembers(String query, Pageable pageable) {
		Page<Member> page = (query == null || query.isBlank())
			? memberRepo.findAll(pageable)
			: memberRepo.findByMemberNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable);

		List<AdminMemberDto.ListItem> rows = page.map(m -> AdminMemberDto.ListItem.builder()
			.id(m.getId())
			.nickname(m.getMemberName())
			.email(m.getEmail())
			.joinedAt(m.getCreatedAt())
			.status(m.getAccountStatus().name())
			.build()).getContent();

		long totalUsers = memberRepo.count();

		return AdminMemberDto.PageResponse.<AdminMemberDto.ListItem>builder()
			.content(rows)
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.page(pageable.getPageNumber())
			.size(pageable.getPageSize())
			.totalUsers(totalUsers)
			.build();
	}

	// 사용자 세부 활동 로그 관련
	public AdminMemberDto.Detail getDetail(Long memberId) {
		Member member = memberRepo.findById(memberId).orElseThrow(() -> new GeneralException(ErrorCode.MEMBER_NOT_FOUND));

		int lostCount  = lostRepo.countByMemberId(memberId);
		int foundCount = foundRepo.countByMemberId(memberId);
		int reportCount = lostReportRepo.countByReporterId(memberId)
			+ foundReportRepo.countByReporterId(memberId);

		List<AdminMemberDto.PostSummary> postsByMember = getPostsByMember(memberId, null);
		List<AdminMemberDto.ReportSummary> reportsByMember = getReportsByMember(memberId);

		return AdminMemberDto.Detail.builder()
			.id(member.getId())
			.nickname(member.getMemberName())
			.email(member.getEmail())
			.joinedAt(member.getCreatedAt())
			.status(member.getAccountStatus().name())
			.activity(AdminMemberDto.ActivitySummary.builder()
				.lostCount(lostCount)
				.foundCount(foundCount)
				.postAllCount(lostCount + foundCount)
				.reportCount(reportCount)
				.postsByMember(postsByMember)
				.reportsByMember(reportsByMember)
				.build())
			.build();
	}

	// 특정 사용자의 활동 상세보기 중 작성 글 목록 관련
	public List<AdminMemberDto.PostSummary> getPostsByMember(Long memberId, String type) {
		var list = new ArrayList<AdminMemberDto.PostSummary>();

		if (type == null || "LOST".equalsIgnoreCase(type)) {
			for (PostLost postLost : lostRepo.findAllByMemberIdAndDeletedByAdminFalseOrderByCreatedAtDesc(memberId)) {
				list.add(AdminMemberDto.PostSummary.builder()
					.postId(postLost.getId())
					.type("LOST")
					.title(postLost.getTitle())
					.region(postLost.getLostRegion())
					.createdAt(postLost.getCreatedAt())
					.thumbnailUrl(s3Service.generatePresignedUrl(resolvePostLostThumbnailUrl(postLost)))
					.build());
			}
		}
		if (type == null || "FOUND".equalsIgnoreCase(type)) {
			for (PostFound postFound : foundRepo.findAllByMemberIdAndDeletedByAdminFalseOrderByCreatedAtDesc(memberId)) {
				list.add(AdminMemberDto.PostSummary.builder()
					.postId(postFound.getId())
					.type("FOUND")
					.title(postFound.getTitle())
					.region(postFound.getFoundRegion())
					.createdAt(postFound.getCreatedAt())
					.thumbnailUrl(s3Service.generatePresignedUrl(resolvePostFoundThumbnailUrl(postFound)))
					.build());
			}
		}
		list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
		return list;
	}

	// 사용자가 제출한 신고 내역(LOST/FOUND 합쳐 최신순)
	public List<AdminMemberDto.ReportSummary> getReportsByMember(Long memberId) {
		var bucket = new ArrayList<AdminMemberDto.ReportSummary>();

		for (PostLostReport postLostReport : lostReportRepo.findAllByReporterIdOrderByCreatedAtDesc(memberId)) {
			var postLost = postLostReport.getPostLost();
			bucket.add(AdminMemberDto.ReportSummary.builder()
				.reportId(postLostReport.getId())
				.targetType("LOST")
				.targetPostId(postLost.getId())
				.targetTitle(postLost.getTitle())
				.reportType(postLostReport.getReportType().name())
				.reportContent(postLostReport.getReportContent())
				.reportStatus(postLostReport.getReportStatus().name())
				.reportedAt(postLostReport.getCreatedAt())
				.build());
		}

		for (PostFoundReport postFoundReport : foundReportRepo.findAllByReporterIdOrderByCreatedAtDesc(memberId)) {
			var postFound = postFoundReport.getPostFound();
			bucket.add(AdminMemberDto.ReportSummary.builder()
				.reportId(postFoundReport.getId())
				.targetType("FOUND")
				.targetPostId(postFound.getId())
				.targetTitle(postFound.getTitle())
				.reportType(postFoundReport.getReportType().name())
				.reportContent(postFoundReport.getReportContent())
				.reportStatus(postFoundReport.getReportStatus().name())
				.reportedAt(postFoundReport.getCreatedAt())
				.build());
		}

		bucket.sort((a, b) -> b.getReportedAt().compareTo(a.getReportedAt()));
		return bucket;
	}

	@Transactional
	public void updateStatus(Long memberId, String status) {
		Member member = memberRepo.findById(memberId).orElseThrow(() -> new GeneralException(ErrorCode.MEMBER_NOT_FOUND));
		member.changeStatus(status);
	}

	@Transactional
	public void deleteHard(Long memberId) {
		if (!memberRepo.existsById(memberId)) return;
		memberRepo.deleteById(memberId);
	}


	// PostLost 썸네일 선택 로직
	private String resolvePostLostThumbnailUrl(PostLost postLost) {
		// 1) aiImage 가 비어있지 않으면 그걸 사용
		String ai = postLost.getAiImage();
		if (ai != null && !ai.isBlank()) {
			return ai.trim();
		}

		// 2) 없으면 realImage 리스트의 첫 번째 유효한 값 사용
		List<String> real = postLost.getRealImage();
		if (real != null && !real.isEmpty()) {
			String first = real.get(0);
			if (first != null && !first.isBlank()) {
				return first.trim();
			}
		}

		// 3) 둘 다 없으면 null (필요하면 기본 이미지 URL로 대체)
		return null;
	}

	// PostFound 썸네일 선택 로직
	private String resolvePostFoundThumbnailUrl(PostFound postFound) {
		// 1) aiImage 가 비어있지 않으면 그걸 사용
		String ai = postFound.getAiImage();
		if (ai != null && !ai.isBlank()) {
			return ai.trim();
		}

		// 2) 없으면 realImage 리스트의 첫 번째 유효한 값 사용
		List<String> real = postFound.getRealImage();
		if (real != null && !real.isEmpty()) {
			String first = real.get(0);
			if (first != null && !first.isBlank()) {
				return first.trim();
			}
		}

		// 3) 둘 다 없으면 null (필요하면 기본 이미지 URL로 대체)
		return null;
	}
}

