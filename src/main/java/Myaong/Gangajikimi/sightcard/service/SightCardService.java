// src/main/java/Myaong/Gangajikimi/sightcard/service/SightCardService.java
package Myaong.Gangajikimi.sightcard.service;

import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.kakaoapi.service.KakaoApiService;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.member.repository.MemberRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.sightcard.converter.SightCardConverter;
import Myaong.Gangajikimi.sightcard.entity.SightCard;
import Myaong.Gangajikimi.sightcard.repository.SightCardRepository;
import Myaong.Gangajikimi.sightcard.web.dto.SightCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SightCardService {

	private final SightCardRepository sightCardRepository;
	private final PostLostRepository postLostRepository;
	private final MemberRepository memberRepository;
	private final KakaoApiService kakaoApiService;
	private final SightCardConverter sightCardConverter;

	@Transactional
	public SightCardDto.SightCardResponse create(Long reporterId, SightCardDto.CreateRequest req) {
		// 1) 기본 조회
		PostLost postLost = postLostRepository.findById(req.getPostLostId())
			.orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

		Member reporter = memberRepository.findById(reporterId)
			.orElseThrow(() -> new GeneralException(ErrorCode.MEMBER_NOT_FOUND));

		// 2) 날짜/시간 파싱
		LocalDate date = toLocalDate(req.getDate());
		LocalTime time = toLocalTimeFromDateArray(req.getTime());

		// 3) 역지오코딩
		String place = kakaoApiService.getAddrFromKakaoApi(req.getLongitude(), req.getLatitude());

		// 4) 저장
		SightCard saved = sightCardRepository.save(
			SightCard.builder()
				.postLost(postLost)
				.reporter(reporter)
				.foundDate(date)
				.foundTime(time)
				.longitude(req.getLongitude())
				.latitude(req.getLatitude())
				.foundPlace(place)
				.build()
		);

		return sightCardConverter.toCreateResponse(saved);
	}

	private LocalDate toLocalDate(List<Integer> arr) {
		return LocalDate.of(arr.get(0), arr.get(1), arr.get(2));
	}

	// 프론트 time 배열: [yyyy,MM,dd,HH,mm,ss,(nanos)]
	private LocalTime toLocalTimeFromDateArray(List<Integer> arr) {
		int hour   = arr.size() >= 4 ? arr.get(3) : 0;
		int minute = arr.size() >= 5 ? arr.get(4) : 0;
		int second = arr.size() >= 6 ? arr.get(5) : 0;
		return LocalTime.of(hour, minute, second);
	}
}
