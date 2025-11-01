package Myaong.Gangajikimi.notification.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.NotificationType;
import Myaong.Gangajikimi.common.enums.PostType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification")
public class Notification extends BaseEntity {

	@Column(nullable = false)
	private Long receiverId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private NotificationType type;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private Boolean isRead = false;

	private Long postId;

	private Long chatRoomId;

	// 해당 알림이 가리키는 게시글의 타입(LOST/FOUND)
	@Enumerated(EnumType.STRING)
	@Column(name = "post_type", length = 20)
	private PostType postType;

	@Column(name = "matched_post_title")
	private String matchedPostTitle;
}
