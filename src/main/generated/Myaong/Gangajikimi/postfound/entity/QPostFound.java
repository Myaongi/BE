package Myaong.Gangajikimi.postfound.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostFound is a Querydsl query type for PostFound
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostFound extends EntityPathBase<PostFound> {

    private static final long serialVersionUID = -569326331L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostFound postFound = new QPostFound("postFound");

    public final Myaong.Gangajikimi.common.QBaseEntity _super = new Myaong.Gangajikimi.common.QBaseEntity(this);

    public final StringPath aiImage = createString("aiImage");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath dogColor = createString("dogColor");

    public final EnumPath<Myaong.Gangajikimi.common.enums.DogGender> dogGender = createEnum("dogGender", Myaong.Gangajikimi.common.enums.DogGender.class);

    public final Myaong.Gangajikimi.dogtype.entity.QDogType dogType;

    public final DatePath<java.time.LocalDate> foundDate = createDate("foundDate", java.time.LocalDate.class);

    public final StringPath foundRegion = createString("foundRegion");

    public final ComparablePath<org.locationtech.jts.geom.Point> foundSpot = createComparable("foundSpot", org.locationtech.jts.geom.Point.class);

    public final DateTimePath<java.time.LocalDateTime> foundTime = createDateTime("foundTime", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final Myaong.Gangajikimi.member.entity.QMember member;

    public final ListPath<String, StringPath> realImage = this.<String, StringPath>createList("realImage", String.class, StringPath.class, PathInits.DIRECT2);

    public final EnumPath<Myaong.Gangajikimi.common.enums.DogStatus> status = createEnum("status", Myaong.Gangajikimi.common.enums.DogStatus.class);

    public final ListPath<Myaong.Gangajikimi.templocation.entity.TempLocation, Myaong.Gangajikimi.templocation.entity.QTempLocation> tempLocations = this.<Myaong.Gangajikimi.templocation.entity.TempLocation, Myaong.Gangajikimi.templocation.entity.QTempLocation>createList("tempLocations", Myaong.Gangajikimi.templocation.entity.TempLocation.class, Myaong.Gangajikimi.templocation.entity.QTempLocation.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostFound(String variable) {
        this(PostFound.class, forVariable(variable), INITS);
    }

    public QPostFound(Path<? extends PostFound> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostFound(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostFound(PathMetadata metadata, PathInits inits) {
        this(PostFound.class, metadata, inits);
    }

    public QPostFound(Class<? extends PostFound> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dogType = inits.isInitialized("dogType") ? new Myaong.Gangajikimi.dogtype.entity.QDogType(forProperty("dogType")) : null;
        this.member = inits.isInitialized("member") ? new Myaong.Gangajikimi.member.entity.QMember(forProperty("member")) : null;
    }

}

