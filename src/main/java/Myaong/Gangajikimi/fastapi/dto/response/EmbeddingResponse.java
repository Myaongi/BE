package Myaong.Gangajikimi.fastapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingResponse {
    private List<String> sentences;
    private List<Float> image;  // 512차원 이미지 임베딩
    private List<Float> text;   // 512차원 텍스트 임베딩

    /**
     * 이미지 임베딩을 float[] 배열로 변환
     */
    public float[] imageEmbeddingToArray() {
        if (image == null) return null;
        float[] result = new float[image.size()];
        for (int i = 0; i < image.size(); i++) {
            result[i] = image.get(i);
        }
        return result;
    }

    /**
     * 텍스트 임베딩을 float[] 배열로 변환
     */
    public float[] textEmbeddingToArray() {
        if (text == null) return null;
        float[] result = new float[text.size()];
        for (int i = 0; i < text.size(); i++) {
            result[i] = text.get(i);
        }
        return result;
    }
}

