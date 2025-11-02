package Myaong.Gangajikimi.fixedlocation.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedLocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostLost postLost;

    @Column(nullable = false, columnDefinition = "geometry(point,4326)")
    private Point spot;

    @Builder
    private FixedLocation(PostLost postLost, Point spot) {

        this.postLost = postLost;
        this.spot = spot;

    }

    public static FixedLocation of(PostLost postLost, Point spot){

        return FixedLocation.builder()
                .postLost(postLost)
                .spot(spot)
                .build();

    }


}