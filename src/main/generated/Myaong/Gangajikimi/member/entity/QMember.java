package Myaong.Gangajikimi.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 88059251L;

    public static final QMember member = new QMember("member1");

    public final Myaong.Gangajikimi.common.QBaseEntity _super = new Myaong.Gangajikimi.common.QBaseEntity(this);

    public final EnumPath<Myaong.Gangajikimi.common.enums.AccountStatus> accountStatus = createEnum("accountStatus", Myaong.Gangajikimi.common.enums.AccountStatus.class);

    public final ListPath<Myaong.Gangajikimi.chatroom.entity.ChatRoom, Myaong.Gangajikimi.chatroom.entity.QChatRoom> ChatRooms = this.<Myaong.Gangajikimi.chatroom.entity.ChatRoom, Myaong.Gangajikimi.chatroom.entity.QChatRoom>createList("ChatRooms", Myaong.Gangajikimi.chatroom.entity.ChatRoom.class, Myaong.Gangajikimi.chatroom.entity.QChatRoom.class, PathInits.DIRECT2);

    public final ListPath<Myaong.Gangajikimi.chatroom.entity.ChatRoom, Myaong.Gangajikimi.chatroom.entity.QChatRoom> ChatRooms2 = this.<Myaong.Gangajikimi.chatroom.entity.ChatRoom, Myaong.Gangajikimi.chatroom.entity.QChatRoom>createList("ChatRooms2", Myaong.Gangajikimi.chatroom.entity.ChatRoom.class, Myaong.Gangajikimi.chatroom.entity.QChatRoom.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath memberName = createString("memberName");

    public final StringPath password = createString("password");

    public final ListPath<Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport, Myaong.Gangajikimi.postfoundreport.entity.QPostFoundReport> postFoundReports = this.<Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport, Myaong.Gangajikimi.postfoundreport.entity.QPostFoundReport>createList("postFoundReports", Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport.class, Myaong.Gangajikimi.postfoundreport.entity.QPostFoundReport.class, PathInits.DIRECT2);

    public final ListPath<Myaong.Gangajikimi.postfound.entity.PostFound, Myaong.Gangajikimi.postfound.entity.QPostFound> postFounds = this.<Myaong.Gangajikimi.postfound.entity.PostFound, Myaong.Gangajikimi.postfound.entity.QPostFound>createList("postFounds", Myaong.Gangajikimi.postfound.entity.PostFound.class, Myaong.Gangajikimi.postfound.entity.QPostFound.class, PathInits.DIRECT2);

    public final ListPath<Myaong.Gangajikimi.postlostreport.entity.PostLostReport, Myaong.Gangajikimi.postlostreport.entity.QPostLostReport> postLostReports = this.<Myaong.Gangajikimi.postlostreport.entity.PostLostReport, Myaong.Gangajikimi.postlostreport.entity.QPostLostReport>createList("postLostReports", Myaong.Gangajikimi.postlostreport.entity.PostLostReport.class, Myaong.Gangajikimi.postlostreport.entity.QPostLostReport.class, PathInits.DIRECT2);

    public final ListPath<Myaong.Gangajikimi.postlost.entity.PostLost, Myaong.Gangajikimi.postlost.entity.QPostLost> postLosts = this.<Myaong.Gangajikimi.postlost.entity.PostLost, Myaong.Gangajikimi.postlost.entity.QPostLost>createList("postLosts", Myaong.Gangajikimi.postlost.entity.PostLost.class, Myaong.Gangajikimi.postlost.entity.QPostLost.class, PathInits.DIRECT2);

    public final EnumPath<Myaong.Gangajikimi.common.enums.Role> role = createEnum("role", Myaong.Gangajikimi.common.enums.Role.class);

    public final ListPath<Myaong.Gangajikimi.sightcard.entity.SightCard, Myaong.Gangajikimi.sightcard.entity.QSightCard> sightCards = this.<Myaong.Gangajikimi.sightcard.entity.SightCard, Myaong.Gangajikimi.sightcard.entity.QSightCard>createList("sightCards", Myaong.Gangajikimi.sightcard.entity.SightCard.class, Myaong.Gangajikimi.sightcard.entity.QSightCard.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

