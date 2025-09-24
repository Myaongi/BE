package Myaong.Gangajikimi.postfound.web.dto.response;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PostFoundDetailResponse {

    private Long postId;
    private String title;
    private String dogName;
    private DogType dogType;
    private String dogColor;
    private DogGender dogGender;
    private String content;
    private LocalDate foundDate;
    private LocalDateTime foundTime;
    private Double longitude;
    private Double latitude;
    // TODO: 주소 변환 API 연동 후 활성화
    // private String address;
    // TODO: Cloud 스토리지 연동 후 활성화
    // private String aiImage;
    // private List<String> realImages;
    private String authorName;
    private LocalDateTime createdAt;
    private String timeAgo;

    @Builder
    private PostFoundDetailResponse(Long postId, String title, String dogName, DogType dogType, 
                                   String dogColor, DogGender dogGender, String content,
                                   LocalDate foundDate, LocalDateTime foundTime, Double longitude, Double latitude,
                                   // TODO: 주소 변환 API 연동 후 활성화
                                   // String address,
                                   // TODO: Cloud 스토리지 연동 후 활성화
                                   // String aiImage, List<String> realImages, 
                                   String authorName,
                                   LocalDateTime createdAt, String timeAgo) {
        this.postId = postId;
        this.title = title;
        this.dogName = dogName;
        this.dogType = dogType;
        this.dogColor = dogColor;
        this.dogGender = dogGender;
        this.content = content;
        this.foundDate = foundDate;
        this.foundTime = foundTime;
        this.longitude = longitude;
        this.latitude = latitude;
        // TODO: 주소 변환 API 연동 후 활성화
        // this.address = address;
        // TODO: Cloud 스토리지 연동 후 활성화
        // this.aiImage = aiImage;
        // this.realImages = realImages;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.timeAgo = timeAgo;
    }

    public static PostFoundDetailResponse of(Long postId, String title, String dogName, DogType dogType,
                                           String dogColor, DogGender dogGender, String content,
                                           LocalDate foundDate, LocalDateTime foundTime, Double longitude, Double latitude,
                                           // TODO: 주소 변환 API 연동 후 활성화
                                           // String address,
                                           // TODO: Cloud 스토리지 연동 후 활성화
                                           // String aiImage, List<String> realImages, 
                                           String authorName,
                                           LocalDateTime createdAt, String timeAgo) {
        return PostFoundDetailResponse.builder()
                .postId(postId)
                .title(title)
                .dogName(dogName)
                .dogType(dogType)
                .dogColor(dogColor)
                .dogGender(dogGender)
                .content(content)
                .foundDate(foundDate)
                .foundTime(foundTime)
                .longitude(longitude)
                .latitude(latitude)
                // TODO: 주소 변환 API 연동 후 활성화
                // .address(address)
                // TODO: Cloud 스토리지 연동 후 활성화
                // .aiImage(aiImage)
                // .realImages(realImages)
                .authorName(authorName)
                .createdAt(createdAt)
                .timeAgo(timeAgo)
                .build();
    }
}
