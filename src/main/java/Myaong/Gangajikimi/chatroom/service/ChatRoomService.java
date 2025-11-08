package Myaong.Gangajikimi.chatroom.service;

import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomCreateRequest;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomListResponse;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {
	ChatRoomResponse createChatRoom(ChatRoomCreateRequest req, Long memberId);
	List<ChatRoomListResponse> getChatRooms(Long memberId);
	// ChatRoomDeleteResponse softDeleteChatRoom(Long chatroomId, Long requesterId);

	ChatRoomResponse getRoomAndMatchCard(Long chatRoomId, Long requesterId);
}
