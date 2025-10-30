package Myaong.Gangajikimi.postlostembedding.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLostEmbedding extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    private PostLost postLost;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(512)")
    private float[] imageEmbedding;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(512)")
    private float[] textEmbedding;

    @Builder
    private PostLostEmbedding(PostLost postLost, float[] imageEmbedding, float[] textEmbedding) {
        this.postLost = postLost;
        this.imageEmbedding = imageEmbedding;
        this.textEmbedding = textEmbedding;
    }

    public static PostLostEmbedding of(PostLost postLost, float[] imageEmbedding, float[] textEmbedding) {

        return PostLostEmbedding.builder()
                .postLost(postLost)
                .imageEmbedding(imageEmbedding)
                .textEmbedding(textEmbedding)
                .build();
    }

}
