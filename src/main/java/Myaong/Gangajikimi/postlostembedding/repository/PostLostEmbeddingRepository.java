package Myaong.Gangajikimi.postlostembedding.repository;

import Myaong.Gangajikimi.postlostembedding.entity.PostLostEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLostEmbeddingRepository extends JpaRepository<PostLostEmbedding,Long> {

    public PostLostEmbedding findPostLostEmbeddingByPostLostId(Long postLostId);

}
