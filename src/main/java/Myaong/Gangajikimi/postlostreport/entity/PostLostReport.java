package Myaong.Gangajikimi.postlostreport.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.ReportStatus;
import Myaong.Gangajikimi.common.enums.ReportType;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLostReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_lost_id", nullable = false)
    private PostLost postLost; // 신고당한 분실 게시글

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
    private PostLostReport(PostLost postLost, Member reporter, ReportType reportType, String reportContent) {
        this.postLost = postLost;
        this.reporter = reporter;
        this.reportType = reportType;
        this.reportContent = reportContent;
    }

    public static PostLostReport of(PostLost postLost, Member reporter, ReportType reportType, String reportContent) {
        return PostLostReport.builder()
                .postLost(postLost)
                .reporter(reporter)
                .reportType(reportType)
                .reportContent(reportContent)
                .build();
    }

    public void changeReportStatus(ReportStatus status) {
        this.reportStatus = status;
    }
}

