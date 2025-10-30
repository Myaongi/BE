package Myaong.Gangajikimi.postfoundembedding.repository;

import Myaong.Gangajikimi.postfoundembedding.entity.PostFoundEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFoundEmbeddingRepository extends JpaRepository<PostFoundEmbedding, Long> {

    PostFoundEmbedding findPostFoundEmbeddingByPostFoundId(Long postFoundId);

}
