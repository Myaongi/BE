package Myaong.Gangajikimi.ai.web.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityScoreResponse {
    
    @JsonProperty("score")
    private float score;  // 최종 가중 평균 유사도 (0.0 ~ 1.0)
}

