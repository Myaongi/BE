package Myaong.Gangajikimi.postlost.repository;

import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLostRepositoryCustom {

    Page<PostLost> findPostLostByFilter(Pageable pageable,
                                         SortType sortType,
                                         Integer maxDistance,
                                         TimeFilter timeFilter,
                                         Double userLongitude,
                                         Double userLatitude);

}
