package Myaong.Gangajikimi.postfoundreport.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.ReportStatus;
import Myaong.Gangajikimi.common.enums.ReportType;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFoundReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_found_id", nullable = false)
    private PostFound postFound; // 신고당한 목격 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter; // 신고한 사용자

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType; // 신고 유형

    @Column(columnDefinition = "TEXT")
    private String reportContent; // 신고 내용 (선택사항)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus = ReportStatus.PENDING; // 신고 상태

    @Builder
    private PostFoundReport(PostFound postFound, Member reporter, ReportType reportType, String reportContent) {
        this.postFound = postFound;
        this.reporter = reporter;
        this.reportType = reportType;
        this.reportContent = reportContent;
    }

    public static PostFoundReport of(PostFound postFound, Member reporter, ReportType reportType, String reportContent) {
        return PostFoundReport.builder()
                .postFound(postFound)
                .reporter(reporter)
                .reportType(reportType)
                .reportContent(reportContent)
                .build();
    }

    public void changeReportStatus(ReportStatus status) {
        this.reportStatus = status;
    }
}

