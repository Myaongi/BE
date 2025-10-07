package Myaong.Gangajikimi.sightcard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import Myaong.Gangajikimi.sightcard.entity.SightCard;

public interface SightCardRepository extends JpaRepository<SightCard, Long> {
	Optional<SightCard> findByChatRoom_Id(Long chatRoomId);
}
