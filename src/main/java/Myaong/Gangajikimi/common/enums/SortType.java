package Myaong.Gangajikimi.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {

    LATEST("latest"), // 최신순
    DISTANCE("distsance");// 거리순

    private final String description;

}
