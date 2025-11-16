package Myaong.Gangajikimi.postlost.web.dto.request;

import Myaong.Gangajikimi.common.enums.DogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "잃어버렸어요 게시글 강아지 상태 업데이트 요청 DTO")
public class PostLostDogStatusUpdateRequest {

    @NotEmpty(message = "게시글 ID 목록은 필수입니다")
    @Schema(description = "업데이트할 게시글 ID 목록", example = "[1, 2, 3]")
    private List<Long> postLostIds;

    @NotNull(message = "강아지 상태는 필수입니다")
    @Schema(description = "강아지 상태", example = "RETURNED", allowableValues = {"MISSING", "SIGHTED", "RETURNED"})
    private DogStatus dogStatus;
}

