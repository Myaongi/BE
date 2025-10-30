package Myaong.Gangajikimi.matchingpost.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_lost_id", nullable = false)
    private PostLost postLost; // 잃어버렸어요 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_found_id", nullable = false)
    private PostFound postFound; // 목격했어요 게시글

    @Column(name = "matching_ratio", nullable = true)
    private Float matchingRatio; // 매칭률

    @Builder
    private MatchingPost(PostLost postLost, PostFound postFound, Float matchingRatio) {
        this.postLost = postLost;
        this.postFound = postFound;
        this.matchingRatio = matchingRatio;
    }

    public static MatchingPost of(PostLost postLost, PostFound postFound, Float matchingRatio) {
        return MatchingPost.builder()
                .postLost(postLost)
                .postFound(postFound)
                .matchingRatio(matchingRatio)
                .build();
    }
}
