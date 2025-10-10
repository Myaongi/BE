package Myaong.Gangajikimi.postfound.repository;

import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface PostFoundRepositoryCustom {

    Page<PostFound> findPostFoundByFilter(Pageable pageable,
                                           SortType sortType,
                                           Integer maxDistance,
                                           TimeFilter timeFilter,
                                           Double userLongitude,
                                           Double userLatitude);

}
