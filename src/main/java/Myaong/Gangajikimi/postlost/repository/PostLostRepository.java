package Myaong.Gangajikimi.postlost.repository;

import Myaong.Gangajikimi.postlost.entity.PostLost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLostRepository extends JpaRepository<PostLost,Long>, PostLostRepositoryCustom {

    Optional<PostLost> findPostLostById(Long id);
    
    // 메인 페이지용 게시글 조회 (최신순)
    Page<PostLost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // 마이페이지용 내 게시글 조회 (최신순)
    Page<PostLost> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

}
