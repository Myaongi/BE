package Myaong.Gangajikimi.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeFilter {

    ONE_HOUR("hour"),
    ONE_DAY("day"),
    ONE_WEEK("week"),
    ONE_MONTH("month");

    private final String period;
}
