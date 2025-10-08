package Myaong.Gangajikimi.memberlocation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Myaong.Gangajikimi.memberlocation.entity.MemberLocation;
import io.lettuce.core.dynamic.annotation.Param;

public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {
	Optional<MemberLocation> findByMemberId(Long memberId);

	@Query(value = """
    	SELECT *
    	FROM member_location ml
    	WHERE ml.member_id <> :excludeMemberId
     	 AND ST_DWithin(
         	   ml.geom::geography,
         	   ST_MakePoint(:longitude, :latitude)::geography,
          	  :radiusMeters
     	 )
  	  """, nativeQuery = true)
	List<MemberLocation> findWithinRadius(
		@Param("latitude") double latitude,
		@Param("longitude") double longitude,
		@Param("radiusMeters") double radiusMeters,
		@Param("excludeMemberId") long excludeMemberId
	);
}