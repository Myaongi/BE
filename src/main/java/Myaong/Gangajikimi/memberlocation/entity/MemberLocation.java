package Myaong.Gangajikimi.memberlocation.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member_location", uniqueConstraints = {
	@UniqueConstraint(columnNames = "member_id")
})
public class MemberLocation extends BaseEntity {

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(columnDefinition = "geometry(Point,4326)")
	private Point geom;

}
