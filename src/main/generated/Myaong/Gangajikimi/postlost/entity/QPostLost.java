package Myaong.Gangajikimi.postlost.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostLost is a Querydsl query type for PostLost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostLost extends EntityPathBase<PostLost> {

    private static final long serialVersionUID = -2127477465L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostLost postLost = new QPostLost("postLost");

    public final Myaong.Gangajikimi.common.QBaseEntity _super = new Myaong.Gangajikimi.common.QBaseEntity(this);

    public final StringPath aiImage = createString("aiImage");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final BooleanPath deletedByAdmin = createBoolean("deletedByAdmin");

    public final StringPath dogColor = createString("dogColor");

    public final EnumPath<Myaong.Gangajikimi.common.enums.DogGender> dogGender = createEnum("dogGender", Myaong.Gangajikimi.common.enums.DogGender.class);

    public final StringPath dogInfo = createString("dogInfo");

    public final StringPath dogName = createString("dogName");

    public final Myaong.Gangajikimi.dogtype.entity.QDogType dogType;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DatePath<java.time.LocalDate> lostDate = createDate("lostDate", java.time.LocalDate.class);

    public final StringPath lostRegion = createString("lostRegion");

    public final ComparablePath<org.locationtech.jts.geom.Point> lostSpot = createComparable("lostSpot", org.locationtech.jts.geom.Point.class);

    public final DateTimePath<java.time.LocalDateTime> lostTime = createDateTime("lostTime", java.time.LocalDateTime.class);

    public final Myaong.Gangajikimi.member.entity.QMember member;

    public final ListPath<Myaong.Gangajikimi.postlostreport.entity.PostLostReport, Myaong.Gangajikimi.postlostreport.entity.QPostLostReport> postLostReports = this.<Myaong.Gangajikimi.postlostreport.entity.PostLostReport, Myaong.Gangajikimi.postlostreport.entity.QPostLostReport>createList("postLostReports", Myaong.Gangajikimi.postlostreport.entity.PostLostReport.class, Myaong.Gangajikimi.postlostreport.entity.QPostLostReport.class, PathInits.DIRECT2);

    public final ListPath<String, StringPath> realImage = this.<String, StringPath>createList("realImage", String.class, StringPath.class, PathInits.DIRECT2);

    public final ListPath<Myaong.Gangajikimi.sightcard.entity.SightCard, Myaong.Gangajikimi.sightcard.entity.QSightCard> sightCards = this.<Myaong.Gangajikimi.sightcard.entity.SightCard, Myaong.Gangajikimi.sightcard.entity.QSightCard>createList("sightCards", Myaong.Gangajikimi.sightcard.entity.SightCard.class, Myaong.Gangajikimi.sightcard.entity.QSightCard.class, PathInits.DIRECT2);

    public final EnumPath<Myaong.Gangajikimi.common.enums.DogStatus> status = createEnum("status", Myaong.Gangajikimi.common.enums.DogStatus.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostLost(String variable) {
        this(PostLost.class, forVariable(variable), INITS);
    }

    public QPostLost(Path<? extends PostLost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostLost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostLost(PathMetadata metadata, PathInits inits) {
        this(PostLost.class, metadata, inits);
    }

    public QPostLost(Class<? extends PostLost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dogType = inits.isInitialized("dogType") ? new Myaong.Gangajikimi.dogtype.entity.QDogType(forProperty("dogType")) : null;
        this.member = inits.isInitialized("member") ? new Myaong.Gangajikimi.member.entity.QMember(forProperty("member")) : null;
    }

}

