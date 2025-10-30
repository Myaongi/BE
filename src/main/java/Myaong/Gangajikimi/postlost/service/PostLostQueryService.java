package Myaong.Gangajikimi.postlost.service;

import Myaong.Gangajikimi.common.dto.request.FilterRequest;
import Myaong.Gangajikimi.common.dto.response.PageResponse;
import Myaong.Gangajikimi.postlost.web.dto.response.PostLostHomeResponse;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.common.util.TimeUtil;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.postlost.web.dto.response.PostLostDetailResponse;
import Myaong.Gangajikimi.s3file.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.locationtech.jts.geom.Point;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLostQueryService {

    private final PostLostRepository postLostRepository;
    private final S3Service s3Service;

    public PostLost findPostLostById(Long postId) {

        PostLost postLost = postLostRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        return postLost;

    }

    public PostLostDetailResponse getPostLostDetail(Long postId) {
        PostLost postLost = postLostRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        // 이미지 Presigned URL 생성 (실제 이미지만)
        List<String> realImageUrls = null;
        if (postLost.getRealImage() != null && !postLost.getRealImage().isEmpty()) {
            realImageUrls = s3Service.generatePresignedUrls(postLost.getRealImage());
        }

        // TODO: AI 이미지 생성 로직 구현 후 활성화
        // String aiImageUrl = null;
        // if (postLost.getAiImage() != null && !postLost.getAiImage().isEmpty()) {
        //     aiImageUrl = s3Service.generatePresignedUrl(postLost.getAiImage());
        // }

        return PostLostDetailResponse.of(
                postLost.getId(),
                postLost.getTitle(),
                postLost.getDogName(),
                postLost.getDogType() != null ? postLost.getDogType().getType() : "알 수 없음",
                postLost.getDogColor(),
                postLost.getDogGender(),
                postLost.getStatus(),
                postLost.getContent(),
                postLost.getLostDate(),
                postLost.getLostTime(),
                postLost.getLostSpot().getX(), // longitude
                postLost.getLostSpot().getY(), // latitude
                postLost.getLostRegion(), // 행정구역 정보
                // TODO: AI 이미지 생성 로직 구현 후 활성화
                // aiImageUrl,
                realImageUrls,
                postLost.getMember().getId(), // authorId
                postLost.getMember().getMemberName(),
                postLost.getCreatedAt(),
                TimeUtil.getTimeAgo(postLost.getCreatedAt())
        );
    }

    /**
     * 잃어버렸어요 게시글 목록 조회 (메인 페이지용)
     */
    public PageResponse getLostPosts(int page, int size, FilterRequest request) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<PostLost> lostPosts = postLostRepository.findPostLostByFilter(pageable,
                                                                            request.getSortType(),
                                                                            request.getMaxDistance(),
                                                                            request.getTimeFilter(),
                                                                            request.getUserLongitude(),
                                                                            request.getUserLatitude());

        // TODO: 필터링 기능 구현 예정
        
        List<PostLostHomeResponse> lostResponses = lostPosts.getContent().stream()
        // PostLost를 PostLostHomeResponse로 변환 (PresignedUrl 포함)
            .map(postLost -> {
                // 첫 번째 이미지의 PresignedUrl 생성
                String presignedImageUrl = null;
                if (postLost.getRealImage() != null && !postLost.getRealImage().isEmpty()) {
                    presignedImageUrl = s3Service.generatePresignedUrl(postLost.getRealImage().get(0));
                }
                return PostLostHomeResponse.of(postLost, presignedImageUrl);
            })
            .toList();
        
        // hasNext 계산: Spring Data JPA Page 객체의 hasNext() 메서드 사용
        boolean hasNext = lostPosts.hasNext();
        
        return PageResponse.of(lostResponses, hasNext);
    }

    /**
     * 마이페이지용 내 잃어버렸어요 게시글 조회
     */
    public PageResponse getMyLostPosts(Long memberId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<PostLost> lostPosts = postLostRepository.findByMemberIdAndDeletedByAdminFalseOrderByCreatedAtDesc(memberId, pageable);
        
        // PostLost를 PostLostHomeResponse로 변환 (PresignedUrl 포함)
        var lostResponses = lostPosts.getContent().stream()
            .map(postLost -> {
                // 첫 번째 이미지의 PresignedUrl 생성
                String presignedImageUrl = null;
                if (postLost.getRealImage() != null && !postLost.getRealImage().isEmpty()) {
                    presignedImageUrl = s3Service.generatePresignedUrl(postLost.getRealImage().get(0));
                }
                return PostLostHomeResponse.of(postLost, presignedImageUrl);
            })
            .toList();
        
        // hasNext 계산: Spring Data JPA Page 객체의 hasNext() 메서드 사용
        boolean hasNext = lostPosts.hasNext();
        
        return PageResponse.of(lostResponses, hasNext);
    }


    /**
     * 매칭을 위한 Lost Post 목록 조회 (모든 활성 게시글)
     */
    public List<PostLost> findAllForMatching() {
        return postLostRepository.findByDeletedByAdminFalseOrderByCreatedAtDesc(
                Pageable.unpaged()).getContent();
    }

    /**
     * 초기 탐색 반경 내의 Lost Post 조회 (QueryDSL 사용)
     */
    public List<PostLost> findWithinInitialRadius(Point centerPoint, double radiusKm) {
        return postLostRepository.findWithinRadius(centerPoint, radiusKm);
    }

    /**
     * 두 좌표 간의 거리 계산 (km 단위)
     */
    public Double calculateDistance(Point point1, Point point2) {
        return postLostRepository.calculateDistance(point1, point2);
    }

}
