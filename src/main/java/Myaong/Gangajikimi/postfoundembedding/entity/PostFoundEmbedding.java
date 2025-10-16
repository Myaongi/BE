package Myaong.Gangajikimi.postfoundembedding.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "vector(512)")
    private float[] imageEmbedding;

    @Column(columnDefinition = "vector(512)")
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
