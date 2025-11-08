package Myaong.Gangajikimi.matchingpost.repository;

import Myaong.Gangajikimi.matchingpost.entity.MatchingPost;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchingPostRepository extends JpaRepository<MatchingPost, Long> {

    public List<MatchingPost> findAllByPostFound(PostFound postFound);

    public List<MatchingPost> findAllByPostLost(PostLost postLost);

    Optional<MatchingPost> findByPostLostAndPostFound(PostLost postLost, PostFound postFound);



}
