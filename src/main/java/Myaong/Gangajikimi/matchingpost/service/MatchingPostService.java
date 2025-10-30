package Myaong.Gangajikimi.matchingpost.service;

import Myaong.Gangajikimi.common.dto.response.PageResponse;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.common.util.TimeUtil;
import Myaong.Gangajikimi.fastapi.service.FastApiService;
import Myaong.Gangajikimi.matchingpost.entity.MatchingPost;
import Myaong.Gangajikimi.matchingpost.repository.MatchingPostRepository;
import Myaong.Gangajikimi.matchingpost.web.dto.request.MatchingPostRequest;
import Myaong.Gangajikimi.matchingpost.web.dto.response.MatchingResponse;
import Myaong.Gangajikimi.matchingpost.web.dto.response.MatchingResultResponse;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.service.PostFoundQueryService;
import Myaong.Gangajikimi.postfoundembedding.entity.PostFoundEmbedding;
import Myaong.Gangajikimi.postfoundembedding.service.PostFoundEmbeddingService;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.service.PostLostQueryService;
import Myaong.Gangajikimi.postlostembedding.entity.PostLostEmbedding;
import Myaong.Gangajikimi.postlostembedding.service.PostLostEmbeddingService;
import Myaong.Gangajikimi.s3file.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingPostService {

    private final PostLostQueryService postLostQueryService;
    private final PostFoundQueryService postFoundQueryService;
    private final FastApiService fastApiService;
    private final PostFoundEmbeddingService postFoundEmbeddingService;
    private final PostLostEmbeddingService postLostEmbeddingService;
    private final S3Service s3Service;

    private final MatchingPostRepository matchingPostRepository;

    // km/h - 평균 이동 속도 (연구 기반: 6km/day = 0.25km/h)
    private static final double V_AVG = 0.25;
    // km - 최대 탐색 반경 (현실적 한계)
    private static final double R_MAX = 10.0;
    // km - 초기 집중 반경 (UX 가이드용, 70% 발견 거리)
    private static final double R_INITIAL = 1.6;
    // h - 최대 유효 탐색 시간 (5일, 90% 회수율)
    private static final double T_MAX = 120.0;

    /**
     * 매칭하고 결과를 저장까지만 한다.
     */
    public MatchingResponse matchingPosts(PostLost postLost, List<PostFound> postFounds){

        // 1) 잃어버렸어요 임베딩 조회
        PostLostEmbedding postLostEmbedding = postLostEmbeddingService.findPostLostEmbeddingByPostLost(postLost);
        float[] lostImageEmbedding = postLostEmbedding.getImageEmbedding();
        float[] lostTextEmbedding = postLostEmbedding.getTextEmbedding();

        // 2) 후보 발견했어요 각각에 대해 유사도 계산 → 매칭 엔티티 생성 → 저장
        List<MatchingPost> result = postFounds.stream()
                .map(foundPost -> {
                    PostFoundEmbedding postFoundEmbedding = postFoundEmbeddingService.findPostFoundEmbeddingByPostFound(foundPost);
                    double score = fastApiService
                            .calculateSimilarity(
                                    lostImageEmbedding,
                                    lostTextEmbedding,
                                    postFoundEmbedding.getImageEmbedding(),
                                    postFoundEmbedding.getTextEmbedding()
                            )
                            .getScore();

                    return MatchingPost.of(postLost, foundPost, (float)(score * 100f));
                })
                .map(matchingPostRepository::save)
                .toList();

        return MatchingResponse.of(postLost, result.size());
    }

    public MatchingResponse matchingPosts(PostFound postFound, List<PostLost> postLosts){

        // 1) 발견했어요 임베딩 조회
        PostFoundEmbedding postFoundEmbedding = postFoundEmbeddingService.findPostFoundEmbeddingByPostFound(postFound);
        float[] foundImageEmbedding = postFoundEmbedding.getImageEmbedding();
        float[] foundTextEmbedding = postFoundEmbedding.getTextEmbedding();

        // 2) 후보 습득글 각각에 대해 유사도 계산 → 매칭 엔티티 생성 → 저장
        List<MatchingPost> result = postLosts.stream()
                .map(post -> {
                    PostLostEmbedding postLostEmbedding = postLostEmbeddingService.findPostLostEmbeddingByPostLost(post);
                    double score = fastApiService
                            .calculateSimilarity(
                                    foundImageEmbedding,
                                    foundTextEmbedding,
                                    postLostEmbedding.getImageEmbedding(),
                                    postLostEmbedding.getTextEmbedding()
                            )
                            .getScore();

                    return MatchingPost.of(post, postFound, (float) score);
                })
                .map(matchingPostRepository::save)
                .toList();

        return MatchingResponse.of(postFound, result.size());
    }

    /**
     * 게시글 ID를 받아서 매칭 가능한 게시글들을 탐색하는 메인 메소드
     */
    public MatchingResponse findMatchingPosts(MatchingPostRequest request) {
        Long postId = request.getPostId();
        String postType = request.getPostType();

        if ("LOST".equals(postType)) {

            // (PostLost) 1 : N (PostFound)
            return findMatchingPostFound(postId);

        } else if ("FOUND".equals(postType)) {

            // (PostFound) 1 : N (PostLost)
            return findMatchingPostLost(postId);

        } else {
            throw new GeneralException(ErrorCode.VALIDATION_FAILED);
        }
    }


    /**
     * PostLost에 대해 매칭 가능한 PostFound들을 찾는 메소드
     * 시간 기반 반경 탐색 방식
     * (PostLost) 1 : N (PostFound)
     */
    private MatchingResponse findMatchingPostFound(Long lostPostId) {
        PostLost lostPost = postLostQueryService.findPostLostById(lostPostId);

        // 1. 현재 시각 기준으로 시간 차이 계산하여 동적 반경 결정
        LocalDateTime currentTime = LocalDateTime.now();

        double timeDifference = calculateTimeDifference(lostPost.getLostTime(), currentTime);
        double dynamicRadius = timeDifference * V_AVG;

        // 2. 반경 내의 Found Post들을 조회
        return matchingPosts(lostPost, postFoundQueryService.findWithinInitialRadius(
                lostPost.getLostSpot(), dynamicRadius));
    }


    /**
     * Found Post에 대해 매칭 가능한 Lost Post들을 찾는 메소드
     * 고정 반경 탐색 후 검증 방식
     */
    private MatchingResponse findMatchingPostLost(Long foundPostId) {

        PostFound foundPost = postFoundQueryService.findPostFoundById(foundPostId);

        // 1. 최대 탐색 반경(10km) 내의 모든 Lost Post들을 조회
        List<PostLost> candidateLostPosts = postLostQueryService.findWithinInitialRadius(
                foundPost.getFoundSpot(), R_MAX);

        // 2. 후보 게시글들의 타당성 검증 후 반환

        // 3. 후보 리스트 반환
        return matchingPosts(foundPost, postLostQueryService.findWithinInitialRadius(
                foundPost.getFoundSpot(), R_MAX
        ));
    }

    /**
     * 시간 차이를 시간 단위로 계산
     */
    private double calculateTimeDifference(LocalDateTime lostTime, LocalDateTime foundTime) {
        Duration duration = Duration.between(lostTime, foundTime);
        return duration.toHours();
    }

    public PageResponse getMatchingPostsByLost(Long postLostId, Integer page, Integer size, Long memberId){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        PostLost postLost = postLostQueryService.findPostLostById(postLostId);

        if (!postLost.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorCode.INVALID_ACCESS);
        }

        List<MatchingPost> allMatches = matchingPostRepository.findAllByPostLost(postLost);

        int fromIndex = Math.max(0, pageable.getPageNumber() * pageable.getPageSize());
        int toIndex = Math.min(allMatches.size(), fromIndex + pageable.getPageSize());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }

        List<MatchingPost> pageSlice = allMatches.subList(fromIndex, toIndex);

        List<MatchingResultResponse> content = pageSlice.stream()
                .map(matching -> {
                    Long matchingPostId = matching.getId();
                    PostFound postFound = matching.getPostFound();

                    String dogType = postFound.getDogType() != null ? postFound.getDogType().getType() : null;
                    String location = postFound.getFoundRegion();
                    float similarity = matching.getMatchingRatio() != null ? matching.getMatchingRatio() * 100f : 0f;

                    String image = postFound.getAiImage() != null ? s3Service.generatePresignedUrl(postFound.getAiImage())
                            : (postFound.getRealImage() != null && !postFound.getRealImage().isEmpty()
                                ? s3Service.generatePresignedUrl(postFound.getRealImage().get(0))
                                : null);

                    String timeAgo = TimeUtil.getTimeAgo(postFound.getFoundTime()) ;

                    return MatchingResultResponse.of(
                            matchingPostId,
                            postFound.getId(),
                            PostType.FOUND,
                            postFound.getTitle(),
                            dogType,
                            postFound.getDogColor(),
                            location,
                            similarity,
                            image,
                            timeAgo
                    );
                })
                .toList();

        boolean hasNext = toIndex < allMatches.size();

        return PageResponse.of(content, hasNext);
    }

    public PageResponse getMatchingPostsByFound(Long postFoundId, Integer page, Integer size, Long memberId){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        PostFound postFound = postFoundQueryService.findPostFoundById(postFoundId);

        if (!postFound.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorCode.INVALID_ACCESS);
        }

        List<MatchingPost> allMatches = matchingPostRepository.findAllByPostFound(postFound);

        int fromIndex = Math.max(0, pageable.getPageNumber() * pageable.getPageSize());
        int toIndex = Math.min(allMatches.size(), fromIndex + pageable.getPageSize());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }

        List<MatchingPost> pageSlice = allMatches.subList(fromIndex, toIndex);

        List<MatchingResultResponse> content = pageSlice.stream()
                .map(matching -> {
                    Long matchingId = matching.getId();
                    PostLost postLost = matching.getPostLost();

                    String dogType = postLost.getDogType() != null ? postLost.getDogType().getType() : null;
                    String location = postLost.getLostRegion();
                    float similarity = matching.getMatchingRatio() != null ? matching.getMatchingRatio() * 100f : 0f;

                    String image = postLost.getAiImage() != null ? s3Service.generatePresignedUrl(postLost.getAiImage())
                            : (postLost.getRealImage() != null && !postLost.getRealImage().isEmpty()
                                ? s3Service.generatePresignedUrl(postLost.getRealImage().get(0))
                                : null);

                    String timeAgo = TimeUtil.getTimeAgo(postLost.getLostTime());

                    return MatchingResultResponse.of(
                            matchingId,
                            postLost.getId(),
                            PostType.LOST,
                            postLost.getTitle(),
                            dogType,
                            postLost.getDogColor(),
                            location,
                            similarity,
                            image,
                            timeAgo
                    );
                })
                .toList();

        boolean hasNext = toIndex < allMatches.size();

        return PageResponse.of(content, hasNext);
    }

    @Transactional
    public void deleteMatchingPost(Long matchingId, Long memberId){

        MatchingPost matchingPost = matchingPostRepository.findById(matchingId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NO_MATCHING_FOUND));

        matchingPostRepository.delete(matchingPost);

    }

}
