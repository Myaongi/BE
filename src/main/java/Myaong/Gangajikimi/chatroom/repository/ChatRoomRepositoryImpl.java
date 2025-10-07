package Myaong.Gangajikimi.chatroom.repository;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import Myaong.Gangajikimi.chatmessage.entity.ChatMessage;
import Myaong.Gangajikimi.chatroom.entity.ChatRoom;
import Myaong.Gangajikimi.chatroom.entity.QChatRoom;
import Myaong.Gangajikimi.chatroom.web.dto.ChatRoomListResponse;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.member.entity.Member;

import Myaong.Gangajikimi.postlost.entity.QPostLost;
import Myaong.Gangajikimi.postfound.entity.QPostFound;

import lombok.RequiredArgsConstructor;

import static Myaong.Gangajikimi.chatroom.entity.QChatRoom.chatRoom;
import static Myaong.Gangajikimi.chatmessage.entity.QChatMessage.chatMessage;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ChatRoomListResponse> findChatRoomsWithLastMessageAndUnreadCount(Long memberId) {
		// 내가 속한 채팅방 목록
		List<ChatRoom> rooms = queryFactory
			.selectFrom(chatRoom)
			.where(chatRoom.member1.id.eq(memberId)
				.or(chatRoom.member2.id.eq(memberId)))
			.fetch();

		QPostLost qLost = QPostLost.postLost;
		QPostFound qFound = QPostFound.postFound;

		return rooms.stream().map(room -> {
			// 상대방 정보
			Member partner = room.getMember1().getId().equals(memberId)
				? room.getMember2()
				: room.getMember1();

			// 최근 메시지 (최신 1개)
			ChatMessage lastMsg = queryFactory
				.selectFrom(chatMessage)
				.where(chatMessage.chatRoom.id.eq(room.getId()))
				.orderBy(chatMessage.createdAt.desc())
				.fetchFirst();

			// 안 읽은 메시지 수
			Long unreadCount = queryFactory
				.select(chatMessage.count())
				.from(chatMessage)
				.where(chatMessage.chatRoom.id.eq(room.getId())
					.and(chatMessage.sender.id.ne(memberId))
					.and(chatMessage.readFlag.eq(false)))
				.fetchOne();

			// 게시글 제목, 사진
			String postTitle = null;
			String postImageUrl = null;
			String postRegion = null;

			if (room.getPostType() == PostType.LOST) {
				var lost = queryFactory.selectFrom(qLost)
					.where(qLost.id.eq(room.getPostId()))
					.fetchOne();
				if (lost != null) {
					postTitle = lost.getTitle();
					postImageUrl = lost.getAiImage();
					postRegion = lost.getLostRegion();
				}
			} else {
				var found = queryFactory.selectFrom(qFound)
					.where(qFound.id.eq(room.getPostId()))
					.fetchOne();
				if (found != null) {
					postTitle = found.getTitle();
					postImageUrl = found.getAiImage();
					postRegion = found.getFoundRegion();
				}
			}

			return ChatRoomListResponse.builder()
				.chatroomId(room.getId())
				.partnerId(partner.getId())
				.partnerNickname(partner.getMemberName())
				.lastMessage(lastMsg != null ? lastMsg.getContent() : null)
				.lastMessageTime(lastMsg != null ? lastMsg.getCreatedAt() : null)
				.unreadCount(unreadCount != null ? unreadCount : 0L)
				.postId(room.getPostId())
				.postType(room.getPostType())
				.postTitle(postTitle)
				.postImageUrl(postImageUrl)
				.postRegion(postRegion)
				.build();
		}).toList();
	}

	@Override
	public Optional<ChatRoom> findByMembersAndPost(Member member1, Member member2,
		PostType postType, Long postId) {
		QChatRoom q = QChatRoom.chatRoom;

		Long a = member1.getId();
		Long b = member2.getId();

		ChatRoom result = queryFactory
			.selectFrom(q)
			.where(
				q.postType.eq(postType),
				q.postId.eq(postId),
				q.member1.id.eq(a).and(q.member2.id.eq(b))
					.or(q.member1.id.eq(b).and(q.member2.id.eq(a)))
			)
			.fetchFirst();

		return Optional.ofNullable(result);
	}
}




// @Override
// public Optional<ChatRoom> findByIdAndParticipant(Long chatroomId, Long userId) {
// 	QChatRoom chatRoom = QChatRoom.chatRoom;
//
// 	ChatRoom result = queryFactory
// 		.selectFrom(chatRoom)
// 		.where(
// 			chatRoom.id.eq(chatroomId)
// 				.and(chatRoom.member1.id.eq(userId)
// 					.or(chatRoom.member2.id.eq(userId)))
// 		)
// 		.fetchOne();
//
// 	return Optional.ofNullable(result);
// }
//
// @Override
// public List<ChatRoom> findAllActiveByUser(Long userId) {
// 	QChatRoom chatRoom = QChatRoom.chatRoom;
//
// 	return queryFactory
// 		.selectFrom(chatRoom)
// 		.where(
// 			chatRoom.member1.id.eq(userId)
// 				.and(chatRoom.deletedByUser1.isFalse())
// 				.or(
// 					chatRoom.member2.id.eq(userId)
// 						.and(chatRoom.deletedByUser2.isFalse())
// 				)
// 		)
// 		.fetch();
// }