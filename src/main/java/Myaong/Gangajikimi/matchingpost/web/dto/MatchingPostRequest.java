package Myaong.Gangajikimi.matchingpost.web.dto;

import Myaong.Gangajikimi.common.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingPostRequest {

    private long postId;

    private PostType postType;

}
