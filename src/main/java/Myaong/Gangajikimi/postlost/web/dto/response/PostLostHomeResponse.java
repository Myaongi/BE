package Myaong.Gangajikimi.postlost.web.dto.response;

import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.common.util.TimeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 잃어버렸어요 게시글 홈 화면 응답 DTO
 * 
 * 홈 화면에서 잃어버렸어요 게시글 목록을 표시하기 위한 응답 DTO입니다.
 * 게시글의 기본 정보와 함께 분실 시간 정보를 포함합니다.
 */
@Getter
@NoArgsConstructor
public class PostLostHomeResponse {
    
    private Long id;
    private String title;
    private String dogType;
    private String dogColor;
    private String location; // 행정동/구 단위
    private LocalDateTime lostDateTime;
    private String image;
    private DogStatus status; // 강아지 상태
    private String timeAgo; // 작성 시간 경과 표시
    
    @Builder
    private PostLostHomeResponse(Long id, String title, String dogType, String dogColor,
                                String location, LocalDateTime lostDateTime,
                                String image, DogStatus status, String timeAgo) {
        this.id = id;
        this.title = title;
        this.dogType = dogType;
        this.dogColor = dogColor;
        this.location = location;
        this.lostDateTime = lostDateTime;
        this.image = image;
        this.status = status;
        this.timeAgo = timeAgo;
    }

    /**
     * PostLost 엔티티를 PostLostHomeResponse로 변환 (PresignedUrl 포함)
     */
    public static PostLostHomeResponse of(PostLost postLost, String presignedImageUrl) {
        return PostLostHomeResponse.builder()
            .id(postLost.getId())
            .title(postLost.getTitle())
            .dogType(postLost.getDogType() != null ? postLost.getDogType().getType() : "알 수 없음")
            .dogColor(postLost.getDogColor())
            .location(postLost.getLostRegion() != null ? postLost.getLostRegion() : "위치 정보 없음")
            .lostDateTime(postLost.getLostTime())
            .image(presignedImageUrl) // PresignedUrl 사용
            .status(postLost.getStatus())
            .timeAgo(TimeUtil.getTimeAgo(postLost.getCreatedAt()))
            .build();
    }

    /**
     * PostLost 엔티티를 PostLostHomeResponse로 변환 (기존 방식 - 호환성 유지)
     */
    public static PostLostHomeResponse from(PostLost postLost) {
        return PostLostHomeResponse.builder()
            .id(postLost.getId())
            .title(postLost.getTitle())
            .dogType(postLost.getDogType() != null ? postLost.getDogType().getType() : "알 수 없음")
            .dogColor(postLost.getDogColor())
            .location(postLost.getLostRegion() != null ? postLost.getLostRegion() : "위치 정보 없음")
            .lostDateTime(postLost.getLostTime())
            .image(postLost.getRealImage() != null && !postLost.getRealImage().isEmpty() 
                ? postLost.getRealImage().get(0) : null) // S3 키 이름 그대로
            .status(postLost.getStatus())
            .timeAgo(TimeUtil.getTimeAgo(postLost.getCreatedAt()))
            .build();
    }
}
