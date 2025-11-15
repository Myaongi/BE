package Myaong.Gangajikimi.matchingpost.service;

import Myaong.Gangajikimi.common.dto.response.PageResponse;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.common.util.TimeUtil;
import Myaong.Gangajikimi.ai.service.AiService;
import Myaong.Gangajikimi.matchingpost.entity.MatchingPost;
import Myaong.Gangajikimi.matchingpost.repository.MatchingPostRepository;
import Myaong.Gangajikimi.matchingpost.web.dto.request.MatchingPostRequest;
import Myaong.Gangajikimi.chatmessage.repository.ChatMessageRepository;
import Myaong.Gangajikimi.chatroom.entity.ChatRoom;
import Myaong.Gangajikimi.chatroom.repository.ChatRoomRepository;
import Myaong.Gangajikimi.matchingpost.web.dto.response.MatchingCountResponse;
import Myaong.Gangajikimi.matchingpost.web.dto.response.MatchingResponse;
import Myaong.Gangajikimi.matchingpost.web.dto.response.MatchingResultResponse;
import Myaong.Gangajikimi.matchingpost.web.dto.response.PostLostMatchingResultResponse;
import Myaong.Gangajikimi.notification.service.NotificationService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchingPostService {

    private final PostLostQueryService postLostQueryService;
    private final PostFoundQueryService postFoundQueryService;
    private final AiService aiService;
    private final PostFoundEmbeddingService postFoundEmbeddingService;
    private final PostLostEmbeddingService postLostEmbeddingService;
    private final S3Service s3Service;
    private final NotificationService notificationService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final MatchingPostRepository matchingPostRepository;

    // km/h - 평균 이동 속도 (연구 기반: 6km/day = 0.25km/h)
    private static final double V_AVG = 10.0;//0.25;
    // km - 최대 탐색 반경 (현실적 한계)
    private static final double R_MAX = 50.0;// R_MAX = 10.0;
    // km - 초기 집중 반경 (UX 가이드용, 70% 발견 거리)
    private static final double R_INITIAL = 1.6;
    // h - 최대 유효 탐색 시간 (5일, 90% 회수율)
    private static final double T_MAX = 120.0;

    private static final float MATCHING_RATIO_THRESHOLD = 80f;

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
                .filter(foundPost -> matchingPostRepository.findByPostLostAndPostFound(postLost, foundPost).isEmpty())
                .map(foundPost -> {
                    PostFoundEmbedding postFoundEmbedding = postFoundEmbeddingService.findPostFoundEmbeddingByPostFound(foundPost);
                    double score = aiService
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

        for (MatchingPost m : result) {
            notificationService.notifyNewMatchForLostOwner(m.getPostLost(), m.getPostFound());
        }

        return MatchingResponse.of(postLost, result.size());
    }

    public MatchingResponse matchingPosts(PostFound postFound, List<PostLost> postLosts){

        // 1) 발견했어요 임베딩 조회
        PostFoundEmbedding postFoundEmbedding = postFoundEmbeddingService.findPostFoundEmbeddingByPostFound(postFound);
        float[] foundImageEmbedding = postFoundEmbedding.getImageEmbedding();
        float[] foundTextEmbedding = postFoundEmbedding.getTextEmbedding();

        // 2) 후보 습득글 각각에 대해 유사도 계산 → 매칭 엔티티 생성 → 저장
        List<MatchingPost> result = postLosts.stream()
                .filter(post -> matchingPostRepository.findByPostLostAndPostFound(post, postFound).isEmpty())
                .map(post -> {
                    PostLostEmbedding postLostEmbedding = postLostEmbeddingService.findPostLostEmbeddingByPostLost(post);
                    double score = aiService
                            .calculateSimilarity(
                                    foundImageEmbedding,
                                    foundTextEmbedding,
                                    postLostEmbedding.getImageEmbedding(),
                                    postLostEmbedding.getTextEmbedding()
                            )
                            .getScore();

                    return MatchingPost.of(post, postFound, (float) (score * 100f));
                })
                .map(matchingPostRepository::save)
                .toList();

        for (MatchingPost m : result) {
            notificationService.notifyNewMatchForFoundOwner(m.getPostFound(), m.getPostLost());
        }

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
        Long authorId = lostPost.getMember().getId();

        // 1. 현재 시각 기준으로 시간 차이 계산하여 동적 반경 결정
        LocalDateTime currentTime = LocalDateTime.now();

        double timeDifference = calculateTimeDifference(lostPost.getLostTime(), currentTime);
        double dynamicRadius = timeDifference * V_AVG;

        // 2. 반경 내의 Found Post들을 조회
        List<PostFound> foundPosts = postFoundQueryService.findWithinInitialRadius(
                lostPost.getLostSpot(), dynamicRadius);

        // 3. 동일 작성자 게시글 필터링
        List<PostFound> filteredFoundPosts = foundPosts.stream()
                .filter(foundPost -> !foundPost.getMember().getId().equals(authorId))
                .toList();

        return matchingPosts(lostPost, filteredFoundPosts);
    }


    /**
     * Found Post에 대해 매칭 가능한 Lost Post들을 찾는 메소드
     * 고정 반경 탐색 후 검증 방식
     */
    private MatchingResponse findMatchingPostLost(Long foundPostId) {

        PostFound foundPost = postFoundQueryService.findPostFoundById(foundPostId);
        Long authorId = foundPost.getMember().getId();

        // 1. 최대 탐색 반경(R_MAX) 내의 모든 Lost Post들을 조회
        List<PostLost> candidateLostPosts = postLostQueryService.findWithinInitialRadius(
                foundPost.getFoundSpot(), R_MAX);

        // 2. 동일 작성자 게시글 필터링
        List<PostLost> filteredLostPosts = candidateLostPosts.stream()
                .filter(lostPost -> !lostPost.getMember().getId().equals(authorId))
                .toList();

        // 3. 후보 리스트 반환
        return matchingPosts(foundPost, filteredLostPosts);
    }

    /**
     * 시간 차이를 시간 단위로 계산
     */
    private double calculateTimeDifference(LocalDateTime lostTime, LocalDateTime foundTime) {
        Duration duration = Duration.between(lostTime, foundTime);
        return duration.toHours();
    }

    public PostLostMatchingResultResponse getMatchingPostsByLost(Long postLostId, Integer page, Integer size, Long memberId){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        PostLost postLost = postLostQueryService.findPostLostById(postLostId);

        if (!postLost.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorCode.INVALID_ACCESS);
        }

        List<MatchingPost> filteredMatches = matchingPostRepository.findAllByPostLost(postLost).stream()
                .filter(match -> match.getMatchingRatio() != null && match.getMatchingRatio() >= MATCHING_RATIO_THRESHOLD)
                .toList();

        int fromIndex = Math.max(0, pageable.getPageNumber() * pageable.getPageSize());
        int toIndex = Math.min(filteredMatches.size(), fromIndex + pageable.getPageSize());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }

        List<MatchingPost> pageSlice = filteredMatches.subList(fromIndex, toIndex);

        List<MatchingResultResponse> content = pageSlice.stream()
                .map(matching -> {
                    Long matchingPostId = matching.getId();
                    PostFound postFound = matching.getPostFound();

                    String dogType = postFound.getDogType() != null ? postFound.getDogType().getType() : null;
                    String location = postFound.getFoundRegion();
                    float similarity = matching.getMatchingRatio() != null ? matching.getMatchingRatio() : 0f;

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
                            postFound.getFoundSpot().getY(),
                            postFound.getFoundSpot().getX(),
                            similarity,
                            image,
                            timeAgo
                    );
                })
                .toList();

        boolean hasNext = toIndex < filteredMatches.size();

        return PostLostMatchingResultResponse.of(postLost.getDogName(), PageResponse.of(content, hasNext));
    }

    public PageResponse getMatchingPostsByFound(Long postFoundId, Integer page, Integer size, Long memberId){

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        PostFound postFound = postFoundQueryService.findPostFoundById(postFoundId);

        if (!postFound.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorCode.INVALID_ACCESS);
        }

        List<MatchingPost> filteredMatches = matchingPostRepository.findAllByPostFound(postFound).stream()
                .filter(match -> match.getMatchingRatio() != null && match.getMatchingRatio() >= MATCHING_RATIO_THRESHOLD)
                .toList();

        int fromIndex = Math.max(0, pageable.getPageNumber() * pageable.getPageSize());
        int toIndex = Math.min(filteredMatches.size(), fromIndex + pageable.getPageSize());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }

        List<MatchingPost> pageSlice = filteredMatches.subList(fromIndex, toIndex);

        List<MatchingResultResponse> content = pageSlice.stream()
                .map(matching -> {
                    Long matchingId = matching.getId();
                    PostLost postLost = matching.getPostLost();

                    String dogType = postLost.getDogType() != null ? postLost.getDogType().getType() : null;
                    String location = postLost.getLostRegion();
                    float similarity = matching.getMatchingRatio() != null ? matching.getMatchingRatio() : 0f;

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
                            postLost.getLostSpot().getY(),
                            postLost.getLostSpot().getX(),
                            similarity,
                            image,
                            timeAgo
                    );
                })
                .toList();

        boolean hasNext = toIndex < filteredMatches.size();

        return PageResponse.of(content, hasNext);
    }

    @Transactional
    public void deleteMatchingPost(Long matchingId, Long memberId){

        MatchingPost matchingPost = matchingPostRepository.findById(matchingId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NO_MATCHING_FOUND));

        matchingPostRepository.delete(matchingPost);

    }

    /**
     * PostLost에 대한 모든 MatchingPost 삭제
     * 
     * @param postLost 삭제할 MatchingPost의 PostLost
     */
    @Transactional
    public void deleteAllByPostLost(PostLost postLost) {
        List<MatchingPost> matchingPosts = matchingPostRepository.findAllByPostLost(postLost);
        matchingPostRepository.deleteAll(matchingPosts);
    }

    /**
     * PostFound에 대한 모든 MatchingPost 삭제
     * 
     * @param postFound 삭제할 MatchingPost의 PostFound
     */
    @Transactional
    public void deleteAllByPostFound(PostFound postFound) {
        List<MatchingPost> matchingPosts = matchingPostRepository.findAllByPostFound(postFound);
        matchingPostRepository.deleteAll(matchingPosts);
    }

    /**
     * MemberId로 해당 Member의 게시글로 매칭된 게시글의 총 개수를 구하는 메서드
     * 
     * @param memberId 회원 ID
     * @return 매칭된 게시글의 총 개수
     */
    @Transactional(readOnly = true)
    public MatchingCountResponse getTotalMatchingCountByMemberId(Long memberId) {
        
        // 1. Member의 모든 PostLost 조회 (Service 계층을 통해)
        List<PostLost> postLosts = postLostQueryService.findAllByMemberId(memberId);
        
        // 2. Member의 모든 PostFound 조회 (Service 계층을 통해)
        List<PostFound> postFounds = postFoundQueryService.findAllByMemberId(memberId);
        
        // 3. 각 PostLost에 대한 MatchingPost 개수 합산
        long lostMatchingCount = postLosts.stream()
                .mapToLong(postLost -> matchingPostRepository.findAllByPostLost(postLost).size())
                .sum();
        
        // 4. 각 PostFound에 대한 MatchingPost 개수 합산
        long foundMatchingCount = postFounds.stream()
                .mapToLong(postFound -> matchingPostRepository.findAllByPostFound(postFound).size())
                .sum();
        
        // 5. 총합 계산
        long totalCount = lostMatchingCount + foundMatchingCount;
        
        return MatchingCountResponse.of(memberId, totalCount);
    }

    /**
     * PostLost ID로 채팅 기록이 있는 MatchingPost만 조회 (무한 스크롤)
     * 
     * @param postLostId PostLost 게시글 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param memberId 회원 ID (권한 확인용)
     * @return 채팅 기록이 있는 MatchingPost 목록 (PageResponse)
     */
    @Transactional(readOnly = true)
    public PageResponse getMatchingPostsWithChatByPostLost(Long postLostId, Integer page, Integer size, Long memberId) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        
        // 1. PostLost 조회
        PostLost postLost = postLostQueryService.findPostLostById(postLostId);
        
        // 2. 권한 확인
        if (!postLost.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorCode.INVALID_ACCESS);
        }

        // 3. 해당 PostLost와 연결된 모든 MatchingPost 조회
        List<MatchingPost> allMatchingPosts = matchingPostRepository.findAllByPostLost(postLost);

        log.info("매칭된 게시글: {}", allMatchingPosts.size());

        // 4. 채팅 기록이 있는 MatchingPost만 필터링
        List<ChatRoom> chatRoomsForPostLost = chatRoomRepository.findAll().stream()
                .filter(room ->
                        (room.getMatchedPostId() != null && room.getMatchedPostId().equals(postLostId))
                                || room.getPostId().equals(postLostId))
                .toList();
        log.info("필터링 된 목록: {}", chatRoomsForPostLost.size());

        List<Long> chatRoomIdsForPostLost = chatRoomsForPostLost.stream()
                .map(ChatRoom::getId)
                .toList();

        boolean hasChatRooms = !chatRoomIdsForPostLost.isEmpty();
        boolean hasChatMessages = chatRoomIdsForPostLost.stream()
                .anyMatch(roomId -> !chatMessageRepository.findByChatRoomId(
                        roomId,
                        org.springframework.data.domain.Pageable.unpaged()).isEmpty());

        List<MatchingPost> matchingPostsWithChat = allMatchingPosts.stream()
                .filter(matchingPost -> hasChatRooms)
                .filter(matchingPost -> hasChatMessages)
                .toList();

        // 5. 페이지네이션 처리
        int fromIndex = Math.max(0, pageable.getPageNumber() * pageable.getPageSize());
        int toIndex = Math.min(matchingPostsWithChat.size(), fromIndex + pageable.getPageSize());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }

        List<MatchingPost> pageSlice = matchingPostsWithChat.subList(fromIndex, toIndex);

        log.info("필터링된 게시글: {}", pageSlice.size());

        // 6. Response로 변환
        List<MatchingResultResponse> content = pageSlice.stream()
                .map(matching -> {
                    PostFound postFound = matching.getPostFound();
                    String dogType = postFound.getDogType() != null ? postFound.getDogType().getType() : null;
                    String location = postFound.getFoundRegion();
                    float similarity = matching.getMatchingRatio() != null ? matching.getMatchingRatio() : 0f;
                    String image = postFound.getAiImage() != null ? s3Service.generatePresignedUrl(postFound.getAiImage())
                            : (postFound.getRealImage() != null && !postFound.getRealImage().isEmpty()
                                ? s3Service.generatePresignedUrl(postFound.getRealImage().get(0))
                                : null);
                    String timeAgo = TimeUtil.getTimeAgo(postFound.getFoundTime());
                    return MatchingResultResponse.of(
                            matching.getId(),
                            postFound.getId(),
                            PostType.FOUND,
                            postFound.getTitle(),
                            dogType,
                            postFound.getDogColor(),
                            location,
                            postFound.getFoundSpot().getY(),
                            postFound.getFoundSpot().getX(),
                            similarity,
                            image,
                            timeAgo
                    );
                })
                .toList();
        
        boolean hasNext = toIndex < matchingPostsWithChat.size();
        
        return PageResponse.of(content, hasNext);
    }

    /**
     * PostFound ID로 채팅 기록이 있는 MatchingPost만 조회 (무한 스크롤)
     * 
     * @param postFoundId PostFound 게시글 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param memberId 회원 ID (권한 확인용)
     * @return 채팅 기록이 있는 MatchingPost 목록 (PageResponse)
     */
    @Transactional(readOnly = true)
    public PageResponse getMatchingPostsWithChatByPostFound(Long postFoundId, Integer page, Integer size, Long memberId) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        
        // 1. PostFound 조회
        PostFound postFound = postFoundQueryService.findPostFoundById(postFoundId);
        
        // 2. 권한 확인
        if (!postFound.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorCode.INVALID_ACCESS);
        }
        
        // 3. 해당 PostFound와 연결된 모든 MatchingPost 조회
        List<MatchingPost> allMatchingPosts = matchingPostRepository.findAllByPostFound(postFound);
        
        // 4. 채팅 기록이 있는 MatchingPost만 필터링
        List<MatchingPost> matchingPostsWithChat = allMatchingPosts.stream()
                .filter(matchingPost -> {
                    PostLost postLost = matchingPost.getPostLost();
                    Long postLostId = postLost.getId();
                    
                    // PostLost 게시글과 PostFound 게시글의 작성자 찾기
                    Long postLostMemberId = postLost.getMember().getId();
                    Long postFoundMemberId = postFound.getMember().getId();
                    
                    // ChatRoom이 존재하는지 확인 (postType=LOST, postId=postLostId, 두 회원 간)
                    List<ChatRoom> chatRooms = chatRoomRepository.findAll().stream()
                            .filter(room -> room.getPostType() == PostType.LOST
                                    && room.getPostId().equals(postLostId)
                                    && ((room.getMember1().getId().equals(postLostMemberId) 
                                            && room.getMember2().getId().equals(postFoundMemberId))
                                        || (room.getMember1().getId().equals(postFoundMemberId) 
                                            && room.getMember2().getId().equals(postLostMemberId))))
                            .toList();
                    
                    // ChatRoom이 있고, 그 ChatRoom에 ChatMessage가 있는지 확인
                    return chatRooms.stream()
                            .anyMatch(room -> !chatMessageRepository.findByChatRoomId(room.getId(), 
                                    org.springframework.data.domain.Pageable.unpaged()).isEmpty());
                })
                .toList();
        
        // 5. 페이지네이션 처리
        int fromIndex = Math.max(0, pageable.getPageNumber() * pageable.getPageSize());
        int toIndex = Math.min(matchingPostsWithChat.size(), fromIndex + pageable.getPageSize());
        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }
        
        List<MatchingPost> pageSlice = matchingPostsWithChat.subList(fromIndex, toIndex);
        
        // 6. Response로 변환
        List<MatchingResultResponse> content = pageSlice.stream()
                .map(matching -> {
                    PostLost postLost = matching.getPostLost();
                    String dogType = postLost.getDogType() != null ? postLost.getDogType().getType() : null;
                    String location = postLost.getLostRegion();
                    float similarity = matching.getMatchingRatio() != null ? matching.getMatchingRatio() : 0f;
                    String image = postLost.getAiImage() != null ? s3Service.generatePresignedUrl(postLost.getAiImage())
                            : (postLost.getRealImage() != null && !postLost.getRealImage().isEmpty()
                                ? s3Service.generatePresignedUrl(postLost.getRealImage().get(0))
                                : null);
                    String timeAgo = TimeUtil.getTimeAgo(postLost.getLostTime());
                    return MatchingResultResponse.of(
                            matching.getId(),
                            postLost.getId(),
                            PostType.LOST,
                            postLost.getTitle(),
                            dogType,
                            postLost.getDogColor(),
                            location,
                            postLost.getLostSpot().getY(),
                            postLost.getLostSpot().getX(),
                            similarity,
                            image,
                            timeAgo
                    );
                })
                .toList();
        
        boolean hasNext = toIndex < matchingPostsWithChat.size();
        
        return PageResponse.of(content, hasNext);
    }

}
