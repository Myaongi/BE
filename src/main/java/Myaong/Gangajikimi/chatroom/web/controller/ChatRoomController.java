package Myaong.Gangajikimi.chatroom.web.controller;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.chatroom.service.ChatRoomService;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomCreateRequest;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomListResponse;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomResponse;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ChatRoom", description = "채팅방 API")
@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {
	private final ChatRoomService chatRoomService;

	@Operation(summary = "채팅방 생성", description = "두 멤버 사이의 1:1 채팅방을 생성합니다. 이미 있으면 기존 방 반환.")
	@ApiResponse(responseCode = "200", description = "성공적으로 생성/조회됨")
	@PostMapping
	public ResponseEntity<GlobalResponse> createChatRoom(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody ChatRoomCreateRequest req) {

		Long memberId = userDetails.getId();

		ChatRoomResponse response = chatRoomService.createChatRoom(req, memberId);
		return GlobalResponse.onSuccess(SuccessCode.OK, response);
	}

	@Operation(summary = "내 채팅방 목록 조회", description = "특정 멤버가 속한 모든 채팅방을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "조회 성공")
	@GetMapping("/me")
	public ResponseEntity<GlobalResponse> getChatRooms(
		@AuthenticationPrincipal CustomUserDetails userDetails) {

		Long memberId = userDetails.getId();

		List<ChatRoomListResponse> response = chatRoomService.getChatRooms(memberId);
		return GlobalResponse.onSuccess(SuccessCode.OK, response);
	}

	/** 채팅방 매칭 정보 조회(재입장) */
	@GetMapping("/{chatRoomId}")
	public ResponseEntity<GlobalResponse> getMatchCard(
			@AuthenticationPrincipal CustomUserDetails user,
			@PathVariable Long chatRoomId
	) {
		var resp = chatRoomService.getRoomAndMatchCard(chatRoomId, user.getId());
		return GlobalResponse.onSuccess(SuccessCode.OK, resp);
	}

	// @Operation(summary = "내 채팅방 삭제(soft delete)", description = "해당 유저의 채팅방 목록에서 채팅방을 삭제합니다.")
	// @ApiResponse(responseCode = "200", description = "삭제 성공")
	// @DeleteMapping("/{chatroomId}")
	// public ResponseEntity<GlobalResponse> deleteChatRoom(
	// 	@AuthenticationPrincipal CustomUserDetails userDetails,
	// 	@PathVariable Long chatroomId) {
	//
	// 	Long memberId = userDetails.getId();
	//
	// 	ChatRoomDeleteResponse response = chatRoomService.softDeleteChatRoom(chatroomId, memberId);
	// 	return GlobalResponse.onSuccess(SuccessCode.OK, response);
	// }
}


