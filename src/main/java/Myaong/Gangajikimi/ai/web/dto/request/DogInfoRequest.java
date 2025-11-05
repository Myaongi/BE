package Myaong.Gangajikimi.ai.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogInfoRequest {
    private String breed;
    private String colors;
    private String features;
}

