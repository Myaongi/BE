package Myaong.Gangajikimi.common.dto.response;

import Myaong.Gangajikimi.common.enums.DogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "강아지 상태 업데이트 응답 DTO")
public class DogStatusUpdateResponse {

    @Schema(description = "게시글 ID", example = "1")
    private final Long postId;

    @Schema(description = "업데이트된 강아지 상태", example = "RETURNED")
    private final DogStatus dogStatus;

    @Schema(description = "업데이트 시간", example = "2024-01-01T15:30:00")
    private final LocalDateTime updatedAt;

    @Builder
    private DogStatusUpdateResponse(Long postId, DogStatus dogStatus, LocalDateTime updatedAt) {
        this.postId = postId;
        this.dogStatus = dogStatus;
        this.updatedAt = updatedAt;
    }

    public static DogStatusUpdateResponse of(Long postId, DogStatus dogStatus, LocalDateTime updatedAt) {
        return DogStatusUpdateResponse.builder()
                .postId(postId)
                .dogStatus(dogStatus)
                .updatedAt(updatedAt)
                .build();
    }
}
