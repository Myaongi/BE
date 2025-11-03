package Myaong.Gangajikimi.postfound.web.dto.request;

import Myaong.Gangajikimi.common.enums.DogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "발견했어요 게시글 강아지 상태 업데이트 요청 DTO")
public class PostFoundDogStatusUpdateRequest {

    @NotEmpty(message = "게시글 ID 목록은 필수입니다")
    @Schema(description = "업데이트할 게시글 ID 목록", example = "[1, 2, 3]")
    private List<Long> postFoundIds;

    @NotNull(message = "강아지 상태는 필수입니다")
    @Schema(description = "강아지 상태", example = "RETURNED", allowableValues = {"MISSING", "SIGHTED", "RETURNED"})
    private DogStatus dogStatus;

    public PostFoundDogStatusUpdateRequest(List<Long> postFoundIds, DogStatus dogStatus) {
        this.postFoundIds = postFoundIds;
        this.dogStatus = dogStatus;
    }
}

