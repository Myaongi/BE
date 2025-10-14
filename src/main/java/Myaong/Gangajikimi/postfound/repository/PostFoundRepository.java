package Myaong.Gangajikimi.postfound.repository;

import Myaong.Gangajikimi.postfound.entity.PostFound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostFoundRepository extends JpaRepository<PostFound, Long>, PostFoundRepositoryCustom {

    Optional<PostFound> findPostFoundById(Long id);
    
    // 메인 페이지용 게시글 조회 (최신순)
    Page<PostFound> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 마이페이지용 내 게시글 조회 (최신순)
    Page<PostFound> findByMemberIdAndDeletedByAdminFalseOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    int countByMemberId(Long memberId);
    List<PostFound> findAllByMemberIdAndDeletedByAdminFalseOrderByCreatedAtDesc(Long memberId);

    // -- 관리자에서 사용 --
    // 목록(삭제글 제외)
    Page<PostFound> findByDeletedByAdminFalseOrderByCreatedAtDesc(Pageable pageable);

    // 목록 + AI 이미지 필터(삭제글 제외)
    Page<PostFound> findByDeletedByAdminFalseAndAiImageIsNotNullOrderByCreatedAtDesc(Pageable pageable);

}

