package Myaong.Gangajikimi.postfoundreport.repository;

import java.util.List;

import Myaong.Gangajikimi.common.enums.ReportStatus;
import Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFoundReportRepository extends JpaRepository<PostFoundReport, Long> {
    
    boolean existsByPostFoundIdAndReporterId(Long postFoundId, Long reporterId);

    int countByReporterId(Long reporterId);
    List<PostFoundReport> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);
    Page<PostFoundReport> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<PostFoundReport> findByReportStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
}
