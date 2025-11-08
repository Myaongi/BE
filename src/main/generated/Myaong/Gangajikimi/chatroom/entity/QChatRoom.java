package Myaong.Gangajikimi.chatroom.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoom is a Querydsl query type for ChatRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoom extends EntityPathBase<ChatRoom> {

    private static final long serialVersionUID = -2016823291L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoom chatRoom = new QChatRoom("chatRoom");

    public final Myaong.Gangajikimi.common.QBaseEntity _super = new Myaong.Gangajikimi.common.QBaseEntity(this);

    public final EnumPath<Myaong.Gangajikimi.common.enums.ChatContext> context = createEnum("context", Myaong.Gangajikimi.common.enums.ChatContext.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> matchedPostId = createNumber("matchedPostId", Long.class);

    public final Myaong.Gangajikimi.member.entity.QMember member1;

    public final Myaong.Gangajikimi.member.entity.QMember member2;

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<Myaong.Gangajikimi.common.enums.PostType> postType = createEnum("postType", Myaong.Gangajikimi.common.enums.PostType.class);

    public final NumberPath<Float> similarity = createNumber("similarity", Float.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QChatRoom(String variable) {
        this(ChatRoom.class, forVariable(variable), INITS);
    }

    public QChatRoom(Path<? extends ChatRoom> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoom(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoom(PathMetadata metadata, PathInits inits) {
        this(ChatRoom.class, metadata, inits);
    }

    public QChatRoom(Class<? extends ChatRoom> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member1 = inits.isInitialized("member1") ? new Myaong.Gangajikimi.member.entity.QMember(forProperty("member1")) : null;
        this.member2 = inits.isInitialized("member2") ? new Myaong.Gangajikimi.member.entity.QMember(forProperty("member2")) : null;
    }

}

