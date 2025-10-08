package Myaong.Gangajikimi.memberlocation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import Myaong.Gangajikimi.memberlocation.entity.MemberLocation;

public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {
	Optional<MemberLocation> findByMemberId(Long memberId);
}