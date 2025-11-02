package Myaong.Gangajikimi.postlost.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLostUpdateSpotsRequest {

    @NotNull
    Double latitude;

    @NotNull
    Double longitude;

}
