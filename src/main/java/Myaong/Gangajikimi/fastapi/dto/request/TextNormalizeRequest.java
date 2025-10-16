package Myaong.Gangajikimi.fastapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextNormalizeRequest {
    private String breed;
    private String colors;
    private String features;
}

