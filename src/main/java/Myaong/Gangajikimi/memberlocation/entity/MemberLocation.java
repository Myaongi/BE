package Myaong.Gangajikimi.memberlocation.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

}
