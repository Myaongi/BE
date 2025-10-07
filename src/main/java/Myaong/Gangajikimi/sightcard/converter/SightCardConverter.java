package Myaong.Gangajikimi.sightcard.converter;

import Myaong.Gangajikimi.sightcard.entity.SightCard;
import Myaong.Gangajikimi.sightcard.web.dto.SightCardDto;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class SightCardConverter {

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

	public SightCardDto.SightCardResponse toCreateResponse(SightCard sightCard) {
		return SightCardDto.SightCardResponse.builder()
			.sightCardId(sightCard.getId())
			.postLostId(sightCard.getPostLost().getId())
			.reporterId(sightCard.getReporter().getId())
			.foundDate(sightCard.getFoundDate().format(DATE_FMT))
			.foundTime(sightCard.getFoundTime().format(TIME_FMT))
			.foundPlace(sightCard.getFoundPlace())
			.build();
	}
}
