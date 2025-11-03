package Myaong.Gangajikimi.matchingpost.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchingCountResponse {

    private Long memberId;
    private Long totalMatchingCount;

    public static MatchingCountResponse of(Long memberId, Long totalMatchingCount) {
        return MatchingCountResponse.builder()
                .memberId(memberId)
                .totalMatchingCount(totalMatchingCount)
                .build();
    }
}

