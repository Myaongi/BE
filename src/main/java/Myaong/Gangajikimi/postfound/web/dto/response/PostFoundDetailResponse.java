package Myaong.Gangajikimi.postfound.web.dto.response;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.dogtype.entity.DogType;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostFoundDetailResponse {

    private Long postId;
    private String title;
    private String dogName;
    private String dogType;
    private String dogColor;
    private DogGender dogGender;
    private DogStatus dogStatus;
    private String content;
    private LocalDate foundDate;
    private LocalDateTime foundTime;
    private Double longitude;
    private Double latitude;
    private String foundRegion; // 행정구역 정보
    String aiImage; // AI 생성 이미지 Presigned URL
    private List<String> realImages; // 실제 이미지 Presigned URL 목록
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private String timeAgo;

    @Builder
    private PostFoundDetailResponse(Long postId,
                                    String title,
                                    String dogType,
                                    String dogColor,
                                    DogGender dogGender,
                                    DogStatus dogStatus,
                                    String content,
                                    LocalDate foundDate,
                                    LocalDateTime foundTime,
                                    Double longitude,
                                    Double latitude,
                                    String foundRegion,
                                    String aiImage,
                                    List<String> realImages,
                                    Long authorId,
                                    String authorName,
                                    LocalDateTime createdAt, String timeAgo) {
        this.postId = postId;
        this.title = title;
        this.dogType = dogType;
        this.dogColor = dogColor;
        this.dogGender = dogGender;
        this.dogStatus = dogStatus;
        this.content = content;
        this.foundDate = foundDate;
        this.foundTime = foundTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.foundRegion = foundRegion;
        this.aiImage = aiImage;
        this.realImages = realImages;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.timeAgo = timeAgo;
    }

    public static PostFoundDetailResponse of(Long postId,
                                             String title,
                                             String dogType,
                                             String dogColor,
                                             DogGender dogGender,
                                             DogStatus dogStatus,
                                             String content,
                                             LocalDate foundDate,
                                             LocalDateTime foundTime,
                                             Double longitude,
                                             Double latitude,
                                             String foundRegion,
                                             String aiImage,
                                             List<String> realImages,
                                             Long authorId,
                                             String authorName,
                                             LocalDateTime createdAt,
                                             String timeAgo) {
        return PostFoundDetailResponse.builder()
                .postId(postId)
                .title(title)
                .dogType(dogType)
                .dogColor(dogColor)
                .dogGender(dogGender)
                .dogStatus(dogStatus)
                .content(content)
                .foundDate(foundDate)
                .foundTime(foundTime)
                .longitude(longitude)
                .latitude(latitude)
                .foundRegion(foundRegion)
                .aiImage(aiImage)
                .realImages(realImages)
                .authorId(authorId)
                .authorName(authorName)
                .createdAt(createdAt)
                .timeAgo(timeAgo)
                .build();
    }
}
