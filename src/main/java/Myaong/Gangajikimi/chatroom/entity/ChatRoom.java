package Myaong.Gangajikimi.chatroom.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.ChatContext;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

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

    // ChatRoom
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatContext context = ChatContext.NORMAL;

    @Column(name = "matched_post_id")
    private Long matchedPostId;

    @Column(name = "similarity")
    private Float similarity;
}
