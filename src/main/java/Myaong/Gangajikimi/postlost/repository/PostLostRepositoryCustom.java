package Myaong.Gangajikimi.postlost.repository;

import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostLostRepositoryCustom {

    Page<PostLost> findPostLostByFilter(Pageable pageable,
                                         SortType sortType,
                                         Integer maxDistance,
                                         TimeFilter timeFilter,
                                         Double userLongitude,
                                         Double userLatitude);

    /**
     * 지정된 반경 내의 Lost Post 조회 (매칭용)
     */
    List<PostLost> findWithinRadius(Point centerPoint, double radiusKm);

    /**
     * 두 좌표 간의 거리 계산 (km 단위)
     */
    Double calculateDistance(Point point1, Point point2);

}
