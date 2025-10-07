package Myaong.Gangajikimi.sightcard.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import Myaong.Gangajikimi.chatroom.entity.ChatRoom;
import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sight_card")
public class SightCard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) // 분실글
	@JoinColumn(name = "post_lost_id")
	private PostLost postLost;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) // 발견카드 제출자
	@JoinColumn(name = "reporter_id")
	private Member reporter;

	@Column(nullable = false)
	private LocalDate foundDate;

	@Column(nullable = false)
	private LocalTime foundTime;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private Double latitude;

	@Column(length = 300, nullable = false)
	private String foundPlace; // 카카오 역지오코딩 결과

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id", unique = true)
	private ChatRoom chatRoom;  // 이 채팅방의 유일한 발견카드

	// 게시글 작성자 id 반환 메서드
	public Long getPostMemberId() {
		return postLost.getMember().getId();
	}
}
