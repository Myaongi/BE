package Myaong.Gangajikimi.fastapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TextNormalizeResponse {
    private List<String> sentences;
}

