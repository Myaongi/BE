package Myaong.Gangajikimi.fixedlocation.repository;

import Myaong.Gangajikimi.fixedlocation.entity.FixedLocation;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedLocationRepository extends JpaRepository<FixedLocation, Long> {

    List<FixedLocation> findByPostLost(PostLost postLost);
}
