package Myaong.Gangajikimi.postlost.web.dto.response;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostLostDetailResponse {

    private Long postId;
    private String title;
    private String dogName;
    private String dogType;
    private String dogColor;
    private DogGender dogGender;
    private DogStatus dogStatus;
    private String content;
    private LocalDate lostDate;
    private LocalDateTime lostTime;
    private Double longitude;
    private Double latitude;
    private String lostRegion; // 행정구역 정보
    private String aiImage; // AI 생성 이미지 Presigned URL
    private List<String> realImages; // 실제 이미지 Presigned URL 목록
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private String timeAgo;
    private List<Double> longitudes; // FixedLocation의 모든 경도 좌표
    private List<Double> latitudes; // FixedLocation의 모든 위도 좌표

    @Builder
    private PostLostDetailResponse(Long postId, String title, String dogName, String dogType,
                                  String dogColor, DogGender dogGender, DogStatus dogStatus, String content,
                                  LocalDate lostDate, LocalDateTime lostTime, Double longitude, Double latitude,
                                  String lostRegion,
                                  String aiImage,
                                  List<String> realImages,
                                  Long authorId, String authorName,
                                  LocalDateTime createdAt, String timeAgo,
                                  List<Double> longitudes, List<Double> latitudes) {
        this.postId = postId;
        this.title = title;
        this.dogName = dogName;
        this.dogType = dogType;
        this.dogColor = dogColor;
        this.dogGender = dogGender;
        this.dogStatus = dogStatus;
        this.content = content;
        this.lostDate = lostDate;
        this.lostTime = lostTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lostRegion = lostRegion;
        this.aiImage = aiImage;
        this.realImages = realImages;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.timeAgo = timeAgo;
        this.longitudes = longitudes;
        this.latitudes = latitudes;
    }

    public static PostLostDetailResponse of(Long postId, String title, String dogName, String dogType,
                                          String dogColor, DogGender dogGender, DogStatus dogStatus, String content,
                                          LocalDate lostDate, LocalDateTime lostTime, Double longitude, Double latitude,
                                          String lostRegion,
                                          String aiImage,
                                          List<String> realImages,
                                          Long authorId, String authorName,
                                          LocalDateTime createdAt, String timeAgo,
                                          List<Double> longitudes, List<Double> latitudes) {
        return PostLostDetailResponse.builder()
                .postId(postId)
                .title(title)
                .dogName(dogName)
                .dogType(dogType)
                .dogColor(dogColor)
                .dogGender(dogGender)
                .dogStatus(dogStatus)
                .content(content)
                .lostDate(lostDate)
                .lostTime(lostTime)
                .longitude(longitude)
                .latitude(latitude)
                .lostRegion(lostRegion)
                .aiImage(aiImage)
                .realImages(realImages)
                .authorId(authorId)
                .authorName(authorName)
                .createdAt(createdAt)
                .timeAgo(timeAgo)
                .longitudes(longitudes)
                .latitudes(latitudes)
                .build();
    }
}
