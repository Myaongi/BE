package Myaong.Gangajikimi.fixedlocation.service;

import Myaong.Gangajikimi.fixedlocation.entity.FixedLocation;
import Myaong.Gangajikimi.fixedlocation.repository.FixedLocationRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedLocationService {

    private final FixedLocationRepository fixedLocationRepository;

    public FixedLocation save(FixedLocation fixedLocation){

        return fixedLocationRepository.save(fixedLocation);

    }

    /**
     * PostLost에 해당하는 모든 FixedLocation 조회
     * 
     * @param postLost PostLost 엔티티
     * @return FixedLocation 목록
     */
    public List<FixedLocation> findAllByPostLost(PostLost postLost) {
        return fixedLocationRepository.findByPostLost(postLost);
    }

}
