package Myaong.Gangajikimi.matchingpost.web.dto.response;

import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchingResponse {
    
    private Long postId;
    private PostType postType;
    private Integer matchingCount; // 매칭된 게시글 개수

    @Builder
    private MatchingResponse(Long postId, PostType postType, Integer matchingCount) {

        this.postId = postId;
        this.postType = postType;
        this.matchingCount = matchingCount;
    }

    public static MatchingResponse of(Long postId, PostType postType, Integer matchingCount) {

        return MatchingResponse.builder()
                .postId(postId)
                .postType(postType)
                .matchingCount(matchingCount)
                .build();
    }

    public static MatchingResponse of(PostLost postLost, Integer matchingCount) {

        return MatchingResponse.builder()
                .postId(postLost.getId())
                .postType(PostType.LOST)
                .matchingCount(matchingCount)
                .build();
    }

    public static MatchingResponse of(PostFound postFound, Integer matchingCount) {

        return MatchingResponse.builder()
                .postId(postFound.getId())
                .postType(PostType.FOUND)
                .matchingCount(matchingCount)
                .build();
    }

}
