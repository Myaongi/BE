package Myaong.Gangajikimi.memberlocation.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Myaong.Gangajikimi.memberlocation.converter.MemberLocationConverter;
import Myaong.Gangajikimi.memberlocation.entity.MemberLocation;
import Myaong.Gangajikimi.memberlocation.repository.MemberLocationRepository;
import Myaong.Gangajikimi.memberlocation.web.dto.MemberLocationDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberLocationService {

	private final MemberLocationRepository memberlocationrepository;
	private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


	@Transactional
	public MemberLocationDto.Response updateLocation(Long memberId, Double latitude, Double longitude) {

		Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

		MemberLocation location = memberlocationrepository.findByMemberId(memberId)
			.map(existing -> {
				existing.setLatitude(latitude);
				existing.setLongitude(longitude);
				existing.setGeom(point);
				return existing;
			})
			.orElseGet(() -> memberlocationrepository.save(MemberLocationConverter.toEntity(memberId, latitude, longitude, point)));

		return MemberLocationConverter.toResponse(location);
	}
}
