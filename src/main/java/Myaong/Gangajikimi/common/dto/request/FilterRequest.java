package Myaong.Gangajikimi.common.dto.request;

import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FilterRequest {

    // 정렬 기준
    @Builder.Default
    SortType sortType = SortType.LATEST;

    // 몇 km 내의 게시글까지 보여줄 건지
    @Builder.Default
    @Min(1)@Max(5)
    Integer maxDistance = null;

    @Builder.Default
    TimeFilter timeFilter = null;

    @Builder.Default
    Double userLongitude = null;

    @Builder.Default
    Double userLatitude = null;

}
