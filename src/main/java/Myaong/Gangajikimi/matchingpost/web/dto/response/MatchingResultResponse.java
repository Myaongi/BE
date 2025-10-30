package Myaong.Gangajikimi.matchingpost.web.dto.response;

import Myaong.Gangajikimi.common.enums.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MatchingResultResponse {

    private Long matchingId;
    private Long postId;
    private PostType postType;
    private String title;
    private String dogType;
    private String dogColor;
    private String location; // 행정동/구 단위
    private float similarity;
    private String image;
    private String timeAgo; // 분실 또는 발견 시간 경과 표시

    @Builder
    private MatchingResultResponse(Long matchingId,
                                   Long postId,
                                   PostType postType,
                                   String title,
                                   String dogType,
                                   String dogColor,
                                   String location,
                                   float similarity,
                                   String image,
                                   String timeAgo) {

        this.matchingId = matchingId;
        this.postId = postId;
        this.postType = postType;
        this.title = title;
        this.dogType = dogType;
        this.dogColor = dogColor;
        this.location = location;
        this.similarity = similarity;
        this.image = image;
        this.timeAgo = timeAgo;
    }

    public static MatchingResultResponse of(Long matchingId,
                                            Long postId,
                                            PostType postType,
                                            String title,
                                            String dogType,
                                            String dogColor,
                                            String location,
                                            float similarity,
                                            String image,
                                            String timeAgo){

        return MatchingResultResponse.builder()
                .matchingId(matchingId)
                .postId(postId)
                .postType(postType)
                .title(title)
                .dogType(dogType)
                .dogColor(dogColor)
                .location(location)
                .similarity(similarity)
                .image(image)
                .timeAgo(timeAgo)
                .build();
    }

}
