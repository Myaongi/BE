package Myaong.Gangajikimi.admin.post.web.dto;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AdminPostDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListItem {
        private Long postId;
        private String type;           // LOST / FOUND
        private String status;         // DogStatus name()
        private String thumbnailUrl;   // aiImage or first(realImage)
        private String title;
        private String authorName;     // member.memberName
        private LocalDateTime createdAt;
        private String region;         // lostRegion / foundRegion
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PageResponse<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;
        private int page;
        private int size;
    }

    // 상세 보기 공통 응답(타입별 필드는 region 이름만 다르므로 통일)
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Detail {
        private Long postId;
        private String type;           // LOST / FOUND
        private String status;         // DogStatus
        private String title;
        private String authorName;
        private LocalDateTime createdAt;

        private String region;         // lostRegion/foundRegion 통합
        private String aiImage;        // null 가능
        private List<String> realImages;

        private String dogName;        // 강아지 이름
        private String breed;          // 품종
        private String color;          // 색상
        private DogGender gender;         // 성별
        private String description;    // 상세 설명(내용)
        private LocalDateTime eventDateTime;  // 실종 or 목격 날짜, 시간

        // 좌표
        private Double latitude;       // foundLatitude / lostLatitude
        private Double longitude;      // foundLongitude / lostLongitude
    }
}