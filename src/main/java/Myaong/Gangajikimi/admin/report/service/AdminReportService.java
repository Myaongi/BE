// src/main/java/Myaong/Gangajikimi/admin/report/service/AdminReportService.java
package Myaong.Gangajikimi.admin.report.service;

import Myaong.Gangajikimi.admin.report.web.dto.AdminReportDto;
import Myaong.Gangajikimi.common.enums.ReportStatus;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport;
import Myaong.Gangajikimi.postfoundreport.repository.PostFoundReportRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.postlostreport.entity.PostLostReport;
import Myaong.Gangajikimi.postlostreport.repository.PostLostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReportService {

	private final PostLostReportRepository lostReportRepo;
	private final PostFoundReportRepository foundReportRepo;
	private final PostLostRepository lostRepo;
	private final PostFoundRepository foundRepo;

	/** 목록: LOST/FOUND 합쳐 최신순 페이징(간단 병합) */
	public AdminReportDto.PageResponse<AdminReportDto.ListItem> list(int page, int size) {
		Pageable doubled = PageRequest.of(page, size * 2);

		Page<PostLostReport>  lostPage  = lostReportRepo.findAllByOrderByCreatedAtDesc(doubled);
		Page<PostFoundReport> foundPage = foundReportRepo.findAllByOrderByCreatedAtDesc(doubled);

		List<AdminReportDto.ListItem> merged = new ArrayList<>(lostPage.getNumberOfElements() + foundPage.getNumberOfElements());
		lostPage.getContent().forEach(r -> merged.add(toListItemLost(r)));
		foundPage.getContent().forEach(r -> merged.add(toListItemFound(r)));
		merged.sort((a, b) -> b.getReportedAt().compareTo(a.getReportedAt()));

		List<AdminReportDto.ListItem> content = merged.size() > size ? merged.subList(0, size) : merged;

		long total = lostPage.getTotalElements() + foundPage.getTotalElements();
		int totalPages = (int) Math.ceil((double) total / size);

		return AdminReportDto.PageResponse.<AdminReportDto.ListItem>builder()
			.content(content)
			.totalElements(total)
			.totalPages(totalPages)
			.page(page)
			.size(size)
			.build();
	}

	/** 신고 내역 상세 조회 */
	public AdminReportDto.Detail detail(String type, Long reportId) {
		if ("LOST".equalsIgnoreCase(type)) {
			PostLostReport postLostReport = lostReportRepo.findById(reportId)
				.orElseThrow(() -> new GeneralException(ErrorCode.REPORT_NOT_FOUND));
			PostLost postLost = postLostReport.getPostLost();
			return AdminReportDto.Detail.builder()
				.reportId(postLostReport.getId())
				.type("LOST")
				.reason(postLostReport.getReportType().getDescription())
				.reporterName(postLostReport.getReporter().getMemberName())
				.reportedAt(postLostReport.getCreatedAt())
				.targetPostId(postLost.getId())
				.targetTitle(postLost.getTitle())
				.targetContent(postLost.getContent())
				.imagePreview(resolveThumb(postLost.getAiImage(), postLost.getRealImage()))
				.realImages(postLost.getRealImage())
				.status(postLostReport.getReportStatus().getDescription())
				.detailReason(postLostReport.getReportContent())
				.build();
		}
		if ("FOUND".equalsIgnoreCase(type)) {
			PostFoundReport postFoundReport = foundReportRepo.findById(reportId)
				.orElseThrow(() -> new GeneralException(ErrorCode.REPORT_NOT_FOUND));
			PostFound postFound = postFoundReport.getPostFound();
			return AdminReportDto.Detail.builder()
				.reportId(postFoundReport.getId())
				.type("FOUND")
				.reason(postFoundReport.getReportType().getDescription())
				.reporterName(postFoundReport.getReporter().getMemberName())
				.reportedAt(postFoundReport.getCreatedAt())
				.targetPostId(postFound.getId())
				.targetTitle(postFound.getTitle())
				.targetContent(postFound.getContent())
				.imagePreview(resolveThumb(postFound.getAiImage(), postFound.getRealImage()))
				.realImages(postFound.getRealImage())
				.status(postFoundReport.getReportStatus().getDescription())
				.detailReason(postFoundReport.getReportContent())
				.build();
		}
		throw new GeneralException(ErrorCode.NOT_EXIST_POSTTYPE);
	}

	/** 액션: (1) 대상 게시글 삭제 + 신고 상태 COMPLETED 로 변경 */
	@Transactional
	public void completeByDeletingTarget(String type, Long reportId) {
		if ("LOST".equalsIgnoreCase(type)) {
			PostLostReport postLostReport = lostReportRepo.findById(reportId)
				.orElseThrow(() -> new GeneralException(ErrorCode.REPORT_NOT_FOUND));
			PostLost target = postLostReport.getPostLost();
			if (target != null && !target.isDeletedByAdmin()) {
				target.markDeletedByAdmin();   // <-- 소프트 삭제
			}
			postLostReport.changeReportStatus(ReportStatus.COMPLETED);
			return;
		}
		if ("FOUND".equalsIgnoreCase(type)) {
			PostFoundReport postFoundReport = foundReportRepo.findById(reportId)
				.orElseThrow(() -> new GeneralException(ErrorCode.REPORT_NOT_FOUND));
			PostFound target = postFoundReport.getPostFound();
			if (target != null && !target.isDeletedByAdmin()) {
				target.markDeletedByAdmin();   // <-- 소프트 삭제
			}
			postFoundReport.changeReportStatus(ReportStatus.COMPLETED);
			return;
		}
		throw new GeneralException(ErrorCode.NOT_EXIST_POSTTYPE);
	}

	/** 액션: (2) 신고 무시 (게시글은 그대로, 신고 상태만 COMPLETED) */
	@Transactional
	public void ignoreAndComplete(String type, Long reportId) {
		if ("LOST".equalsIgnoreCase(type)) {
			PostLostReport postLostReport = lostReportRepo.findById(reportId)
				.orElseThrow(() -> new GeneralException(ErrorCode.REPORT_NOT_FOUND));
			postLostReport.changeReportStatus(ReportStatus.COMPLETED);
			return;
		}
		if ("FOUND".equalsIgnoreCase(type)) {
			PostFoundReport postFoundReport = foundReportRepo.findById(reportId)
				.orElseThrow(() -> new GeneralException(ErrorCode.REPORT_NOT_FOUND));
			postFoundReport.changeReportStatus(ReportStatus.COMPLETED);
			return;
		}
		throw new GeneralException(ErrorCode.NOT_EXIST_POSTTYPE);
	}

	/* ---------- mappers & helpers ---------- */

	private AdminReportDto.ListItem toListItemLost(PostLostReport postLostReport) {
		return AdminReportDto.ListItem.builder()
			.reportId(postLostReport.getId())
			.type("LOST")
			.reason(postLostReport.getReportType().getDescription())
			.targetPostId(postLostReport.getPostLost().getId())
			.targetTitle(postLostReport.getPostLost().getTitle())
			.reporterName(postLostReport.getReporter().getMemberName())
			.reportedAt(postLostReport.getCreatedAt())
			.status(postLostReport.getReportStatus().getDescription())
			.build();
	}

	private AdminReportDto.ListItem toListItemFound(PostFoundReport postFoundReport) {
		return AdminReportDto.ListItem.builder()
			.reportId(postFoundReport.getId())
			.type("FOUND")
			.reason(postFoundReport.getReportType().getDescription())
			.targetPostId(postFoundReport.getPostFound().getId())
			.targetTitle(postFoundReport.getPostFound().getTitle())
			.reporterName(postFoundReport.getReporter().getMemberName())
			.reportedAt(postFoundReport.getCreatedAt())
			.status(postFoundReport.getReportStatus().getDescription())
			.build();
	}

	// 썸네일 사진 관련
	private String resolveThumb(String aiImage, List<String> real) {
		if (aiImage != null && !aiImage.isBlank()) return aiImage.trim();
		if (real != null && !real.isEmpty()) {
			String first = real.get(0);
			if (first != null && !first.isBlank()) return first.trim();
		}
		return null;
	}
}
