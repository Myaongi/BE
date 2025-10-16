package Myaong.Gangajikimi.matchingpost.service;

import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.matchingpost.domain.Post;
import Myaong.Gangajikimi.matchingpost.entity.MatchingPost;
import Myaong.Gangajikimi.matchingpost.web.dto.MatchingPostRequest;
import Myaong.Gangajikimi.postfound.service.PostFoundQueryService;
import Myaong.Gangajikimi.postlost.service.PostLostQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingPostService {

    private final PostFoundQueryService postFoundQueryService;
    private final PostLostQueryService postLostQueryService;

    List<MatchingPost> matchPosts(MatchingPostRequest request){
        
        PostType postType = request.getPostType();
        Post post;
        List<Post> targetPosts;
        
        // 요청된 게시글 조회
        if (postType == PostType.FOUND) {
            post = postFoundQueryService.findPostFoundById(request.getPostId());
        } else {
            post = postLostQueryService.findPostLostById(request.getPostId());
        }

        // 매칭 알고리즘 구현
        // 1. 거리 기반 필터링
        // 2. 강아지 특성 매칭 (품종, 색상, 크기, 성별)
        // 3. 시간 기반 필터링
        // 4. 유사도 점수 계산

        // 매칭 로직 수행 (임시로 빈 리스트 반환)
        return List.of();
    }


}
