package Myaong.Gangajikimi.common.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse {
    
    private List<?> content;
    private boolean hasNext;

    @Builder
    private PageResponse(List<?> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }

    public static PageResponse of(List<?> content, boolean hasNext) {
        return PageResponse.builder()
                .content(content)
                .hasNext(hasNext)
                .build();
    }

}

