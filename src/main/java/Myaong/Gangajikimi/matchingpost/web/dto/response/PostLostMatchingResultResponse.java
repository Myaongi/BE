package Myaong.Gangajikimi.matchingpost.web.dto.response;

import Myaong.Gangajikimi.common.dto.response.PageResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLostMatchingResultResponse {

    String dogName;

    PageResponse pageResponse;

    @Builder
    private PostLostMatchingResultResponse(String dogName, PageResponse pageResponse) {

        this.dogName = dogName;
        this.pageResponse = pageResponse;

    }

    public static PostLostMatchingResultResponse of(String dogName, PageResponse pageResponse) {

        return PostLostMatchingResultResponse.builder()
                .dogName(dogName)
                .pageResponse(pageResponse)
                .build();

    }

}
