package Myaong.Gangajikimi.postlostreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostLostReport is a Querydsl query type for PostLostReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostLostReport extends EntityPathBase<PostLostReport> {

    private static final long serialVersionUID = 740540143L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostLostReport postLostReport = new QPostLostReport("postLostReport");

    public final Myaong.Gangajikimi.common.QBaseEntity _super = new Myaong.Gangajikimi.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final Myaong.Gangajikimi.postlost.entity.QPostLost postLost;

    public final StringPath reportContent = createString("reportContent");

    public final Myaong.Gangajikimi.member.entity.QMember reporter;

    public final EnumPath<Myaong.Gangajikimi.common.enums.ReportStatus> reportStatus = createEnum("reportStatus", Myaong.Gangajikimi.common.enums.ReportStatus.class);

    public final EnumPath<Myaong.Gangajikimi.common.enums.ReportType> reportType = createEnum("reportType", Myaong.Gangajikimi.common.enums.ReportType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostLostReport(String variable) {
        this(PostLostReport.class, forVariable(variable), INITS);
    }

    public QPostLostReport(Path<? extends PostLostReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostLostReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostLostReport(PathMetadata metadata, PathInits inits) {
        this(PostLostReport.class, metadata, inits);
    }

    public QPostLostReport(Class<? extends PostLostReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.postLost = inits.isInitialized("postLost") ? new Myaong.Gangajikimi.postlost.entity.QPostLost(forProperty("postLost"), inits.get("postLost")) : null;
        this.reporter = inits.isInitialized("reporter") ? new Myaong.Gangajikimi.member.entity.QMember(forProperty("reporter")) : null;
    }

}

