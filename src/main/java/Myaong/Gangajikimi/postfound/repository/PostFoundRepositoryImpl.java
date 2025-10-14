package Myaong.Gangajikimi.postfound.repository;

import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.entity.QPostFound;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostFoundRepositoryImpl implements PostFoundRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPostFound postFound = QPostFound.postFound;
    private final GeometryFactory geometryFactory;

    @Override
    public Page<PostFound> findPostFoundByFilter(Pageable pageable,
                                                 SortType sortType,     // 거리순일 경우 거리 계산 필요
                                                 Integer maxDistance,   // 값 존재할 경우 거리 계산 필요
                                                 TimeFilter timeFilter,
                                                 Double userLongitude,
                                                 Double userLatitude) {

        // 1. 필터링 조건 설정
        BooleanBuilder predicate = new BooleanBuilder();

        // 2. 정렬순 설정 객체 생성
        OrderSpecifier<?> orderSpecifier;

        // 3. 거리 계산 표현식 생성 (초기값 : null)
        NumberExpression<Double> distance = null;

        // 거리 계산을 해야 될 경우에만
        if (sortType == SortType.DISTANCE || maxDistance != null) {

            // 사용자 위치 정보가 없을 경우 에러 반환
            if (userLongitude == null || userLatitude == null) {
                throw new GeneralException(ErrorCode.LOCATION_REQUIRED);
            }

            // 4. User 위치 기반으로 좌표 생성
            Point userLocation = geometryFactory.createPoint(new Coordinate(userLongitude, userLatitude));

            // 5. 거리 측정
            distance = Expressions.numberTemplate(Double.class, "ST_DistanceSphere({0}, {1})",
                    postFound.foundSpot, userLocation);

        }

        // 6. 정렬 조건에 따라 정렬 기준 생성 (최신순, 거리순 중에 하나)
        orderSpecifier = decideOrderSpecifier(sortType, distance);

        // 7. 거리 제한 조건 추가
        if (maxDistance != null) {
            predicate.and(distance.loe(maxDistance * 1000));
        }

        // 8. 시간 제한 조건 추가
        if (timeFilter != null) {
            predicate.and(decideTimeFilter(timeFilter));
        }

        // 9. soft delete 조건 추가
        predicate.and(postFound.deletedByAdmin.isFalse());


        List<PostFound> posts = queryFactory
                .selectFrom(postFound)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset()) // 어디서부터 조회할 것인지
                .limit(pageable.getPageSize()) // 몇 개 조회할 것인지 (그 다음 값이 있는지 보기 위해 1을 추가)
                .fetch();

        Long count = queryFactory
                .select(postFound.count())
                .from(postFound)
                .where(predicate)
                .fetchOne();

        //
        return new PageImpl<>(posts, pageable, count != null ? count : 0L);
    }

    OrderSpecifier<?> decideOrderSpecifier(SortType sortType, NumberExpression<Double> distance) {

        if (sortType == SortType.DISTANCE) {
            return distance.asc();
        } else {
            return postFound.createdAt.desc();
        }
    }

    BooleanExpression decideTimeFilter(TimeFilter timeFilter) {

        // 어떤 기간 이후인지에 대한 기준점
        LocalDateTime point = LocalDateTime.now();

        switch (timeFilter) {

            case ONE_HOUR:
                point = point.minusHours(1);
                break;

            case ONE_DAY:
                point = point.minusDays(1);
                break;

            case ONE_WEEK:
                point = point.minusWeeks(1);
                break;

            case ONE_MONTH:
                point = point.minusMonths(1);
                break;

            default:
                throw new GeneralException(ErrorCode.INVALID_TIME_FILTER);
        }

        return postFound.createdAt.goe(point);

    }


    // 거리 제한 필터 조건을 생성하는 메서드
    private BooleanExpression distanceFilter(NumberExpression<Double> distance, Integer maxDistanceKm) {
        if (maxDistanceKm == null || distance == null) {
            return null; // 거리 제한 없음
        }
        // maxDistanceKm를 미터(m) 단위로 변환하여 비교 (loe = less than or equal)
        return distance.loe(maxDistanceKm * 1000.0);
    }
}
