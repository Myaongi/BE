package Myaong.Gangajikimi.chatroom.web.dto;

import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.common.enums.PostType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
	private Long chatroomId;
	private Long member1Id;
	private Long member2Id;
	private LocalDateTime createdAt;

	// ▼ 매칭으로 들어온 경우만 세팅되는 선택 필드(없으면 null) ▼
	private Float      matchingRatio;   // 0~100
	private Long       opponentPostId;
	private PostType opponentPostType; // LOST or FOUND
	private String     opponentTitle;
	private String     opponentRegion;
	private String     opponentDogType;
	private String     opponentDogColor;
	private String     opponentTimeAgo; // "3분 전" 등 (선택)
	private String 	   opponentImage;
	private String     dogName;
	private DogStatus status;
}
