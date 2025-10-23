package Myaong.Gangajikimi.fastapi.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityScoreRequest {
    
    @JsonProperty("emb_a_image")
    private List<Float> embAImage;
    
    @JsonProperty("emb_a_text")
    private List<Float> embAText;
    
    @JsonProperty("emb_b_image")
    private List<Float> embBImage;
    
    @JsonProperty("emb_b_text")
    private List<Float> embBText;

    /**
     * float[] 배열을 받아서 SimilarityScoreRequest 생성 (weights 제거)
     */
    public static SimilarityScoreRequest of(
            float[] embAImage,
            float[] embAText,
            float[] embBImage,
            float[] embBText
    ) {
        return SimilarityScoreRequest.builder()
                .embAImage(toList(embAImage))
                .embAText(toList(embAText))
                .embBImage(toList(embBImage))
                .embBText(toList(embBText))
                .build();
    }

    /**
     * float[] -> List<Float> 변환 헬퍼 메서드
     */
    private static List<Float> toList(float[] array) {
        if (array == null) return null;
        List<Float> list = new ArrayList<>(array.length);
        for (float value : array) {
            // NaN이나 Infinity 체크
            if (Float.isNaN(value) || Float.isInfinite(value)) {
                throw new IllegalArgumentException("임베딩 벡터에 유효하지 않은 값이 포함되어 있습니다: " + value);
            }
            list.add(value);
        }
        return list;
    }
}

