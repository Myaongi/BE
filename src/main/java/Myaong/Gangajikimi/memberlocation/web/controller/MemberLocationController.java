package Myaong.Gangajikimi.memberlocation.web.controller;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.memberlocation.service.MemberLocationService;
import Myaong.Gangajikimi.memberlocation.web.docs.MemberLocationControllerDocs;
import Myaong.Gangajikimi.memberlocation.web.dto.MemberLocationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members/locations")
public class MemberLocationController implements MemberLocationControllerDocs {

	private final MemberLocationService locationService;

	@PostMapping
	@Override
	public ResponseEntity<GlobalResponse> updateLocation(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody MemberLocationDto.UpdateRequest req
	) {
		Long memberId = userDetails.getId();
		var response = locationService.updateLocation(memberId, req.getLatitude(), req.getLongitude());
		return GlobalResponse.onSuccess(SuccessCode.OK, response);
	}
}
