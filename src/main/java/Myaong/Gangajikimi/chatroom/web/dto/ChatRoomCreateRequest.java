package Myaong.Gangajikimi.chatroom.web.dto;

import Myaong.Gangajikimi.common.enums.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomCreateRequest {
	@NotNull(message = "멤버 ID는 필수입니다.")
	private Long memberId;

	@NotNull
	private PostType postType; // LOST | FOUND

	@NotNull
	private Long postId;

	private Long matchingId;

}