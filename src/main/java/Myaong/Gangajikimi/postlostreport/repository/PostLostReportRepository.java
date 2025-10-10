package Myaong.Gangajikimi.postlostreport.repository;

import java.util.List;

import Myaong.Gangajikimi.common.enums.ReportStatus;
import Myaong.Gangajikimi.postlostreport.entity.PostLostReport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLostReportRepository extends JpaRepository<PostLostReport, Long> {
    
    boolean existsByPostLostIdAndReporterId(Long postLostId, Long reporterId);

    int countByReporterId(Long reporterId);
    List<PostLostReport> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);
    Page<PostLostReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<PostLostReport> findByReportStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
}