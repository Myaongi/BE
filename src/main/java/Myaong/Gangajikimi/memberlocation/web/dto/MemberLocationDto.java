package Myaong.Gangajikimi.memberlocation.web.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class MemberLocationDto {

	@Getter
	@Setter
	public static class UpdateRequest {
		@NotNull
		private Double latitude;

		@NotNull
		private Double longitude;
	}

	@Getter
	@Builder
	public static class Response {
		private Long memberId;
		private Double latitude;
		private Double longitude;
		private LocalDateTime updatedAt;
	}
}
