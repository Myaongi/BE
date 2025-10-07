package Myaong.Gangajikimi.sightcard.web.controller;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.sightcard.service.SightCardService;
import Myaong.Gangajikimi.sightcard.web.docs.SightCardControllerDocs;
import Myaong.Gangajikimi.sightcard.web.dto.SightCardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sight-cards")
public class SightCardController implements SightCardControllerDocs {

	private final SightCardService sightCardService;

	@PostMapping
	@Override
	public ResponseEntity<GlobalResponse> createSightCardWithChat(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody SightCardDto.CreateRequest req
	) {
		Long reporterId = userDetails.getId();
		var response = sightCardService.createWithChat(reporterId, req);
		return GlobalResponse.onSuccess(SuccessCode.OK, response);
	}

	@GetMapping("/{chatRoomId}")
	@Override
	public ResponseEntity<GlobalResponse> getSightCardByChatRoom(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long chatRoomId
	) {
		Long participatorId = userDetails.getId();
		var result = sightCardService.getByChatRoom(chatRoomId, participatorId);
		return GlobalResponse.onSuccess(SuccessCode.OK, result);
	}
}
