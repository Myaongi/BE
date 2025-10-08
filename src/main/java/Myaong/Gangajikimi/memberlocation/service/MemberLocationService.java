package Myaong.Gangajikimi.memberlocation.service;

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

	@Transactional
	public MemberLocationDto.Response updateLocation(Long memberId, Double latitude, Double longitude) {
		MemberLocation location = memberlocationrepository.findByMemberId(memberId)
			.map(existing -> {
				existing.setLatitude(latitude);
				existing.setLongitude(longitude);
				return existing;
			})
			.orElseGet(() -> memberlocationrepository.save(MemberLocationConverter.toEntity(memberId, latitude, longitude)));

		return MemberLocationConverter.toResponse(location);
	}
}
