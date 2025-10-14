package Myaong.Gangajikimi.postfound.entity;
import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundUpdateRequest;
import Myaong.Gangajikimi.postfoundreport.entity.PostFoundReport;
import Myaong.Gangajikimi.templocation.entity.TempLocation;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFound extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 사용자 아이디

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_type_id", nullable = true)
    private DogType dogType; // 견종

    @Column(nullable = false)
    private String dogColor;

    @Column
    @Enumerated(EnumType.STRING)
    private DogGender dogGender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DogStatus status; // 강아지 상태 (실종, 목격, 귀가완료)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDate foundDate;

    @Column(nullable = false)
    private LocalDateTime foundTime;

    @Column
    private String foundRegion;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point foundSpot;

    @Column
    private String aiImage;

    @ElementCollection
    private List<String> realImage;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deletedByAdmin = false;

    private LocalDateTime deletedAt;

    public boolean isDeletedByAdmin() {
        return deletedByAdmin;
    }

    /** 운영자 삭제(소프트 삭제) */
    public void markDeletedByAdmin() {
        this.deletedByAdmin = true;
        this.deletedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "postFound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempLocation> tempLocations = new ArrayList<>();

    @OneToMany(mappedBy = "postFound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFoundReport> postFoundReports = new ArrayList<>();

    @Builder
    private PostFound(List<String> realImage,
                      Member member,
                      String title,
                      DogType dogType,
                      String dogColor,
                      String content,
                      DogGender dogGender,
                      Point foundSpot,
                      LocalDate foundDate,
                      LocalDateTime foundTime,
                      String foundRegion){

        this.realImage = realImage;
        this.member = member;
        this.title = title;
        this.dogType = dogType;
        this.dogGender = dogGender;
        this.dogColor = dogColor;
        this.status = DogStatus.SIGHTED; // 게시글 작성 시 기본값: 실종
        this.content = content;
        this.foundSpot = foundSpot;
        this.foundDate = foundDate;
        this.foundTime = foundTime;
        this.foundRegion = foundRegion;
    }

    public static PostFound of(List<String> realImage,
                               Member member,
                               String title,
                               DogType dogType,
                               DogGender dogGender,
                               String dogColor,
                               String content,
                               Point foundSpot,
                               LocalDate foundDate,
                               LocalDateTime foundTime,
                               String foundRegion){

        return PostFound.builder()
                .realImage(realImage)
                .member(member)
                .title(title)
                .dogType(dogType)
                .dogColor(dogColor)
                .dogGender(dogGender)
                .content(content)
                .foundSpot(foundSpot)
                .foundDate(foundDate)
                .foundTime(foundTime)
                .foundRegion(foundRegion)
                .build();
    }

    public void update(PostFoundUpdateRequest request, Point foundSpot, DogType dogType, String foundRegion) {

        DogGender dogGender = DogGender.valueOf(request.getDogGender());

        // 이미지 업데이트는 updateImages() 메서드로 별도 처리
        this.title = request.getTitle();
        this.dogType = dogType;
        this.dogColor = request.getDogColor();
        this.dogGender = dogGender;
        this.content = request.getFeatures();
        this.foundDate = request.getFoundDate();
        this.foundTime = request.getFoundTime();
        this.foundSpot = foundSpot;
        this.foundRegion = foundRegion;
    }

    public void updateImages(List<String> imageKeyNames) {
        this.realImage = imageKeyNames;
    }

    /**
     * 이미지 추가 (기존 이미지에 새 이미지들 추가)
     */
    public void addImages(List<String> imageKeyNames) {
        if (this.realImage == null) {
            this.realImage = new ArrayList<>();
        }
        this.realImage.addAll(imageKeyNames);
    }

    /**
     * 이미지 삭제 (특정 이미지들 제거)
     */
    public void removeImages(List<String> imageKeyNames) {
        if (this.realImage != null) {
            this.realImage.removeAll(imageKeyNames);
        }
    }

    /**
     * 강아지 상태 변경
     */
    public void updateStatus(DogStatus status) {
        this.status = status;
    }

    /**
     * 사용자가 첨부한 사진 중 첫 번째 사진을 반환
    * */
    public String firstRealImage(List<String> realImage){
        return realImage.get(0);
    }
}

