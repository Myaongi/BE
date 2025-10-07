package Myaong.Gangajikimi.chatroom.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member1_id", nullable = false)
    private Member member1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member2_id", nullable = false)
    private Member member2;

    // 채팅이 어떤 게시글에서 시작되었는지(없으면 null)
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType;   // LOST | FOUND

    @Column(name = "post_id", nullable = false)
    private Long postId;         // 해당 글 PK


}
