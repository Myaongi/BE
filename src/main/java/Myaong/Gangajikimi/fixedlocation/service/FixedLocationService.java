package Myaong.Gangajikimi.fixedlocation.service;

import Myaong.Gangajikimi.fixedlocation.entity.FixedLocation;
import Myaong.Gangajikimi.fixedlocation.repository.FixedLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FixedLocationService {

    private final FixedLocationRepository fixedLocationRepository;

    public FixedLocation save(FixedLocation fixedLocation){

        return fixedLocationRepository.save(fixedLocation);

    }

}
