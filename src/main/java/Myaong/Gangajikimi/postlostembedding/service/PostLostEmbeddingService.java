package Myaong.Gangajikimi.postlostembedding.service;

import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlostembedding.entity.PostLostEmbedding;
import Myaong.Gangajikimi.postlostembedding.repository.PostLostEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLostEmbeddingService {

    private final PostLostEmbeddingRepository postLostEmbeddingRepository;

    /**
     * PostLost 임베딩 저장
     * @param postLost 게시글
     * @param imageEmbedding 이미지 임베딩 (768차원)
     * @param textEmbedding 텍스트 임베딩 (768차원)
     * @return 저장된 PostLostEmbedding
     */
    @Transactional
    public PostLostEmbedding saveEmbedding(PostLost postLost, float[] imageEmbedding, float[] textEmbedding) {
        
        PostLostEmbedding embedding = PostLostEmbedding.of(postLost, imageEmbedding, textEmbedding);
        PostLostEmbedding savedEmbedding = postLostEmbeddingRepository.save(embedding);
        
        log.info("PostLost 임베딩 저장 완료 - embeddingId: {}", savedEmbedding.getId());
        return savedEmbedding;
    }

    public PostLostEmbedding findPostLostEmbeddingByPostLost(PostLost postLost) {

        return postLostEmbeddingRepository.findPostLostEmbeddingByPostLostId(postLost.getId());

    }

}
