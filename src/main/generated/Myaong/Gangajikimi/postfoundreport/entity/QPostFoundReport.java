package Myaong.Gangajikimi.postfoundreport.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostFoundReport is a Querydsl query type for PostFoundReport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostFoundReport extends EntityPathBase<PostFoundReport> {

    private static final long serialVersionUID = 1471913701L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostFoundReport postFoundReport = new QPostFoundReport("postFoundReport");

    public final Myaong.Gangajikimi.common.QBaseEntity _super = new Myaong.Gangajikimi.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final Myaong.Gangajikimi.postfound.entity.QPostFound postFound;

    public final StringPath reportContent = createString("reportContent");

    public final Myaong.Gangajikimi.member.entity.QMember reporter;

    public final EnumPath<Myaong.Gangajikimi.common.enums.ReportStatus> reportStatus = createEnum("reportStatus", Myaong.Gangajikimi.common.enums.ReportStatus.class);

    public final EnumPath<Myaong.Gangajikimi.common.enums.ReportType> reportType = createEnum("reportType", Myaong.Gangajikimi.common.enums.ReportType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPostFoundReport(String variable) {
        this(PostFoundReport.class, forVariable(variable), INITS);
    }

    public QPostFoundReport(Path<? extends PostFoundReport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostFoundReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostFoundReport(PathMetadata metadata, PathInits inits) {
        this(PostFoundReport.class, metadata, inits);
    }

    public QPostFoundReport(Class<? extends PostFoundReport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.postFound = inits.isInitialized("postFound") ? new Myaong.Gangajikimi.postfound.entity.QPostFound(forProperty("postFound"), inits.get("postFound")) : null;
        this.reporter = inits.isInitialized("reporter") ? new Myaong.Gangajikimi.member.entity.QMember(forProperty("reporter")) : null;
    }

}

