package Myaong.Gangajikimi.common.dto.request;

import Myaong.Gangajikimi.common.enums.DogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "강아지 상태 업데이트 요청 DTO")
public class DogStatusUpdateRequest {

    @NotNull(message = "강아지 상태는 필수입니다")
    @Schema(description = "강아지 상태", example = "RETURNED", allowableValues = {"MISSING", "SIGHTED", "RETURNED"})
    private DogStatus dogStatus;

    public DogStatusUpdateRequest(DogStatus dogStatus) {
        this.dogStatus = dogStatus;
    }
}
