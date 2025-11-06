package Myaong.Gangajikimi.postfoundembedding.service;

import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfoundembedding.entity.PostFoundEmbedding;
import Myaong.Gangajikimi.postfoundembedding.repository.PostFoundEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFoundEmbeddingService {

    private final PostFoundEmbeddingRepository postFoundEmbeddingRepository;

    /**
     * PostFound 임베딩 저장
     * @param postFound 게시글
     * @param imageEmbedding 이미지 임베딩 (768차원)
     * @param textEmbedding 텍스트 임베딩 (768차원)
     * @return 저장된 PostFoundEmbedding
     */
    @Transactional
    public PostFoundEmbedding saveEmbedding(PostFound postFound, float[] imageEmbedding, float[] textEmbedding) {
        
        PostFoundEmbedding embedding = PostFoundEmbedding.of(postFound, imageEmbedding, textEmbedding);
        PostFoundEmbedding savedEmbedding = postFoundEmbeddingRepository.save(embedding);
        
        log.info("PostFound 임베딩 저장 완료 - embeddingId: {}", savedEmbedding.getId());
        return savedEmbedding;
    }

    public PostFoundEmbedding findPostFoundEmbeddingByPostFound(PostFound postFound) {

        return postFoundEmbeddingRepository.findPostFoundEmbeddingByPostFoundId(postFound.getId());

    }

}
