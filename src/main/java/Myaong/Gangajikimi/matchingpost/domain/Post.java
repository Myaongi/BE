package Myaong.Gangajikimi.matchingpost.domain;

import Myaong.Gangajikimi.common.enums.PostType;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

/**
 * 매칭 기능에서 사용되는 Post의 공통 인터페이스
 * PostFound와 PostLost 엔티티의 공통 필드들을 정의
 */
public interface Post {
    
    // 공통 필드들
    PostType postType = null;
    Long id = null;
    Point location = null;
    LocalDateTime createdAt = null;
    String dogBreed = null;
    String dogColor = null;
    String dogSize = null;
    String dogGender = null;
    String specialNote = null;
    String status = null;
}
