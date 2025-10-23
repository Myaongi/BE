package Myaong.Gangajikimi.postfound.web.dto.response;

import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.common.util.TimeUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 목격했어요 게시글 홈 화면 응답 DTO
 * 
 * 홈 화면에서 목격했어요 게시글 목록을 표시하기 위한 응답 DTO입니다.
 * 게시글의 기본 정보와 함께 목격 시간 정보를 포함합니다.
 */
@Getter
@NoArgsConstructor
public class PostFoundHomeResponse {
    
    private Long id;
    private String title;
    private String dogType;
    private String dogColor;
    private String location; // 행정동/구 단위
    private LocalDateTime foundDateTime;
    private String image;
    private DogStatus status; // 강아지 상태
    private String timeAgo; // 작성 시간 경과 표시
    
    @Builder
    private PostFoundHomeResponse(Long id, String title, String dogType, String dogColor,
                                 String location, LocalDateTime foundDateTime,
                                 String image, DogStatus status, String timeAgo) {
        this.id = id;
        this.title = title;
        this.dogType = dogType;
        this.dogColor = dogColor;
        this.location = location;
        this.foundDateTime = foundDateTime;
        this.image = image;
        this.status = status;
        this.timeAgo = timeAgo;
    }

    /**
     * PostFound 엔티티를 PostFoundHomeResponse로 변환 (PresignedUrl 포함)
     */
    public static PostFoundHomeResponse of(PostFound postFound, String presignedImageUrl) {
        return PostFoundHomeResponse.builder()
            .id(postFound.getId())
            .title(postFound.getTitle())
            .dogType(postFound.getDogType() != null ? postFound.getDogType().getType() : "알 수 없음")
            .dogColor(postFound.getDogColor())
            .location(postFound.getFoundRegion() != null ? postFound.getFoundRegion() : "위치 정보 없음")
            .foundDateTime(postFound.getFoundTime())
            .image(presignedImageUrl) // PresignedUrl 사용
            .status(postFound.getStatus())
            .timeAgo(TimeUtil.getTimeAgo(postFound.getCreatedAt()))
            .build();
    }

    /**
     * PostFound 엔티티를 PostFoundHomeResponse로 변환 (기존 방식 - 호환성 유지)
     */
    public static PostFoundHomeResponse from(PostFound postFound) {
        return PostFoundHomeResponse.builder()
            .id(postFound.getId())
            .title(postFound.getTitle())
            .dogType(postFound.getDogType() != null ? postFound.getDogType().getType() : "알 수 없음")
            .dogColor(postFound.getDogColor())
            .location(postFound.getFoundRegion() != null ? postFound.getFoundRegion() : "위치 정보 없음")
            .foundDateTime(postFound.getFoundTime())
            .image(postFound.getRealImage() != null && !postFound.getRealImage().isEmpty() 
                ? postFound.getRealImage().get(0) : null) // S3 키 이름 그대로
            .status(postFound.getStatus())
            .timeAgo(TimeUtil.getTimeAgo(postFound.getCreatedAt()))
            .build();
    }
}
