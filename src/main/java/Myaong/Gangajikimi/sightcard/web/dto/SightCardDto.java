package Myaong.Gangajikimi.sightcard.web.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SightCardDto {

	@Getter @Setter
	public static class CreateRequest {
		@NotNull
		private Long postLostId;          // 어떤 분실글에 대한 카드인지

		@NotNull @Size(min = 3, max = 3)
		private List<@NotNull Integer> date; // [yyyy, M, d]

		@NotNull @Size(min = 5, max = 7)
		private List<@NotNull Integer> time; // [yyyy,M,d,HH,mm,ss,(nanos)]

		@NotNull
		private Double longitude;

		@NotNull
		private Double latitude;
	}

	@Getter
	@Builder
	public static class SightCardResponse {
		private Long sightCardId; // 발견 카드 id
		private Long postLostId; // 분실게시글 id
		private Long reporterId; // 발견카드 작성자 id

		// 프론트 표시용 포맷
		private String foundDate;   // yyyy.MM.dd
		private String foundTime;   // HH:mm
		private String foundPlace;  // 행정지역/지번 요약
		
	}
}
