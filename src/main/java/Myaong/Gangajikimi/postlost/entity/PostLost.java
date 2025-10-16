package Myaong.Gangajikimi.postlost.entity;

import Myaong.Gangajikimi.common.BaseEntity;
import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;

import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.matchingpost.domain.Post;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateRequest;
import Myaong.Gangajikimi.postlostreport.entity.PostLostReport;
import Myaong.Gangajikimi.sightcard.entity.SightCard;
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
public class PostLost extends BaseEntity implements Post {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 사용자 아이디

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 20)
    private String dogName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_type_id", nullable = true)
    private DogType dogType; // 견종

    @Column(nullable = false)
    private String dogColor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DogGender dogGender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DogStatus status; // 강아지 상태 (실종, 목격, 귀가완료)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDate lostDate;

    @Column(nullable = false)
    private LocalDateTime lostTime; // 분실 시간

    @Column
    private String lostRegion;

    @JoinColumn(name = "lost_spot", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point lostSpot; // 분실 장소

    @Column
    private String dogInfo;

    @Column
    private String aiImage; // AI 사진 (URL 또는 파일 경로)

    @ElementCollection
    private List<String> realImage; // 실제 사진 (URL 또는 파일 경로 리스트)

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

    @OneToMany(mappedBy = "postLost",cascade = CascadeType.ALL)
    private List<SightCard> sightCards;

    @OneToMany(mappedBy = "postLost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLostReport> postLostReports = new ArrayList<>();

    @Builder
    private PostLost(List<String> realImage,
                     Member member,
                     String title,
                     String dogName,
                     DogType dogType,
                     DogGender dogGender,
                     String dogColor,
                     String content,
                     Point lostSpot,
                     LocalDate lostDate,
                     LocalDateTime lostTime,
                     String lostRegion,
                     String dogInfo){
        this.realImage = realImage;
        this.member = member;
        this.title = title;
        this.dogName = dogName;
        this.dogType = dogType;
        this.dogGender = dogGender;
        this.dogColor = dogColor;
        this.status = DogStatus.MISSING; // 게시글 작성 시 기본값: 실종
        this.content = content;
        this.lostSpot = lostSpot;
        this.lostDate = lostDate;
        this.lostTime = lostTime;
        this.lostRegion = lostRegion;
        this.dogInfo = dogInfo;
    }

    public static PostLost of(List<String> realImage,
                              Member member,
                              String title,
                              String dogName,
                              DogType dogType,
                              DogGender dogGender,
                              String dogColor,
                              String content,
                              Point lostSpot,
                              LocalDate lostDate,
                              LocalDateTime lostTime,
                              String lostRegion,
                              String dogInfo){
        return PostLost.builder()
                .realImage(realImage)
                .member(member)
                .title(title)
                .dogName(dogName)
                .dogType(dogType)
                .dogColor(dogColor)
                .dogGender(dogGender)
                .content(content)
                .lostSpot(lostSpot)
                .lostDate(lostDate)
                .lostTime(lostTime)
                .lostRegion(lostRegion)
                .dogInfo(dogInfo)
                .build();
    }

    public void update(PostLostUpdateRequest request,
                       Point lostSpot,
                       DogType dogType,
                       String lostRegion,
                       String dogInfo) {

        DogGender dogGender = DogGender.valueOf(request.getDogGender());

        // 이미지 업데이트는 updateImages() 메서드로 별도 처리
        this.title = request.getTitle();
        this.dogName = request.getDogName();
        this.dogType = dogType;
        this.dogColor = request.getDogColor();
        this.dogGender = dogGender;
        this.content = request.getFeatures();
        this.lostDate = request.getLostDate();
        this.lostTime = request.getLostTime();
        this.lostSpot = lostSpot;
        this.lostRegion = lostRegion;
        this.dogInfo = dogInfo;
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