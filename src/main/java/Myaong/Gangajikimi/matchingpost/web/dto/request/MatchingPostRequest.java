package Myaong.Gangajikimi.matchingpost.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingPostRequest {
    
    private Long postId; // 매칭할 게시글 ID (Lost Post 또는 Found Post)
    private String postType; // "LOST" 또는 "FOUND"
}
