package Myaong.Gangajikimi.postfoundembedding.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.postfound.entity.PostFound;
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
public class PostFoundEmbedding extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    private PostFound postFound;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    private float[] imageEmbedding;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    private float[] textEmbedding;

    @Builder
    private PostFoundEmbedding(PostFound postFound, float[] imageEmbedding, float[] textEmbedding) {
        this.postFound = postFound;
        this.imageEmbedding = imageEmbedding;
        this.textEmbedding = textEmbedding;
    }

    public static PostFoundEmbedding of(PostFound postFound, float[] imageEmbedding, float[] textEmbedding) {

        return PostFoundEmbedding.builder()
                .postFound(postFound)
                .imageEmbedding(imageEmbedding)
                .textEmbedding(textEmbedding)
                .build();
    }

}
