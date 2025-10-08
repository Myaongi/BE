package Myaong.Gangajikimi.memberlocation.converter;

import Myaong.Gangajikimi.memberlocation.entity.MemberLocation;
import Myaong.Gangajikimi.memberlocation.web.dto.MemberLocationDto;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

public class MemberLocationConverter {

	public static MemberLocation toEntity(Long memberId, Double latitude, Double longitude, Point point) {
		return MemberLocation.builder()
			.memberId(memberId)
			.latitude(latitude)
			.longitude(longitude)
			.geom(point)
			.build();
	}

	public static MemberLocationDto.Response toResponse(MemberLocation memberLocation) {
		return MemberLocationDto.Response.builder()
			.memberId(memberLocation.getMemberId())
			.latitude(memberLocation.getLatitude())
			.longitude(memberLocation.getLongitude())
			.updatedAt(memberLocation.getUpdatedAt())
			.build();
	}
}