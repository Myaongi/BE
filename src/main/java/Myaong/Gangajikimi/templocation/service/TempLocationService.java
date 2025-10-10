package Myaong.Gangajikimi.templocation.service;

import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.templocation.entity.TempLocation;
import Myaong.Gangajikimi.templocation.repository.TempLocationRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempLocationService {

    private final TempLocationRepository tempLocationRepository;
    private final GeometryFactory geometryFactory;

    public TempLocation saveTempLocation(double longitude, double latitude, PostFound postFound) {

        Point newPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        TempLocation tempLocation = TempLocation.of(postFound, newPoint);

        return tempLocationRepository.save(tempLocation);

    }

    public void updateTempLocation(double longitude, double latitude, PostFound postFound) {

        TempLocation tempLocation = tempLocationRepository.findTempLocationByPostFound(postFound).orElse(null);

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        if (tempLocation != null) {
            tempLocation.updateSpot(point);
        }
    }

}
