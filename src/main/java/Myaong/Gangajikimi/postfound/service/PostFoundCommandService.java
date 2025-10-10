package Myaong.Gangajikimi.postfound.service;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.dogtype.service.DogTypeService;
import Myaong.Gangajikimi.common.enums.Role;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.notification.service.NotificationService;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundRequest;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundUpdateRequest;
import Myaong.Gangajikimi.s3file.service.S3Service;
import Myaong.Gangajikimi.kakaoapi.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFoundCommandService {

    private final PostFoundRepository postFoundRepository;
    private final DogTypeService dogTypeService;
    private final S3Service s3Service;
    private final KakaoApiService kakaoApiService;
    private final NotificationService notificationService;
    private final GeometryFactory geometryFactory;

    public PostFound postPostFound(PostFoundRequest request, Member member, List<MultipartFile> images){

        DogType dogType = dogTypeService.findByTypeName(request.getDogType());
        DogGender dogGender = DogGender.valueOf(request.getDogGender());

        Point newPoint = geometryFactory.createPoint(new Coordinate(request.getFoundLongitude(), request.getFoundLatitude()));

        // TODO: 주소 변환 API 연동 후 활성화 (예: "서울시 송파구")
        String foundRegion = kakaoApiService.getAddrFromKakaoApi(request.getFoundLongitude(), request.getFoundLatitude());

        // 먼저 PostFound를 저장해서 ID를 얻음
        PostFound newPostFound = PostFound.of(null, // 이미지는 나중에 설정
                member,
                request.getTitle(),
                dogType,
                dogGender,
                request.getDogColor(),
                request.getFeatures(),
                newPoint,
                request.getFoundDate(),
                request.getFoundTime(),
                foundRegion);

        PostFound savedPostFound = postFoundRepository.save(newPostFound);

        // 이미지 업로드 및 keyName 목록 생성
        List<String> imageKeyNames = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            imageKeyNames = images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .map(image -> s3Service.upload(image, "postFound", savedPostFound.getId().toString()))
                    .toList();
        }

        // 업로드된 이미지 keyNames로 PostFound 업데이트
        savedPostFound.updateImages(imageKeyNames);

        /**
         * 반경 3km 유저들에게 알림 전송
         */
        notificationService.notifyNearbyUsers(
            savedPostFound.getId(),
            request.getFoundLatitude(),
            request.getFoundLongitude(),
            member.getId(),
            PostType.FOUND
        );

        return savedPostFound;
    }

    public PostFound updatePostFound(PostFoundUpdateRequest request,
                                     Member member,
                                     PostFound postFound,
                                     List<MultipartFile> images){

        // 권한 확인
        if(!member.equals(postFound.getMember())){
            throw new GeneralException(ErrorCode.UNAUTHORIZED_UPDATING);
        }

        Point point = geometryFactory.createPoint(new Coordinate(request.getFoundLongitude(), request.getFoundLatitude()));

        // TODO: 주소 변환 API 연동 후 활성화 (예: "서울시 송파구")
        String foundRegion = kakaoApiService.getAddrFromKakaoApi(request.getFoundLongitude(), request.getFoundLatitude());

        // 기존 이미지 삭제 (S3에서)
        /*
        if (postFound.getRealImage() != null && !postFound.getRealImage().isEmpty()) {
            postFound.getRealImage().forEach(s3Service::deleteFile);
        }
         */

        // 1. 삭제할 이미지 처리
        List<String> deletedImageKeys = new ArrayList<>();

        // 있으면
        if (request.getDeletedImageUrls() != null && !request.getDeletedImageUrls().isEmpty()) {
            // 삭제할 이미지들의 키 추출
            deletedImageKeys = request.getDeletedImageUrls().stream()
                    .map(s3Service::extractKeyFromUrl)
                    .toList();

            // S3에서 파일 삭제 (아직 DB에는 삭제가 안됨)
            deletedImageKeys.forEach(s3Service::deleteFile);
        }

        // 2. 새 이미지 업로드
        List<String> newImageKeyNames = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            newImageKeyNames = images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .map(image -> s3Service.upload(image, "postFound", postFound.getId().toString()))
                    .toList();
        }
        
        // 3. 순서를 보장하는 이미지 업데이트
        updateImagesWithOrder(postFound, deletedImageKeys, newImageKeyNames);
        
        // 4. 게시글 정보 업데이트 (이미지 제외)
        DogType dogType = dogTypeService.findByTypeName(request.getDogType());
        postFound.update(request, point, dogType, foundRegion);
        


        return postFound;
    }

    /**
     * 순서를 보장하는 이미지 업데이트
     * 삭제된 이미지의 자리에 뒤의 이미지들이 앞으로 이동하고, 새 이미지는 맨 뒤에 추가
     */
    private void updateImagesWithOrder(PostFound postFound, List<String> deletedImageKeys, List<String> newImageKeyNames) {
        List<String> currentImages = new ArrayList<>(postFound.getRealImage());
        log.info("현재 이미지 목록: {}", currentImages);
        log.info("삭제할 이미지들: {}", deletedImageKeys);
        log.info("새로 추가할 이미지들: {}", newImageKeyNames);
        
        // 1. 삭제할 이미지들을 제거
        currentImages.removeAll(deletedImageKeys);
        log.info("삭제 후 이미지 목록: {}", currentImages);
        
        // 2. 새 이미지들을 맨 뒤에 추가
        currentImages.addAll(newImageKeyNames);
        log.info("최종 이미지 목록: {}", currentImages);
        
        // 3. 업데이트된 이미지 목록으로 설정
        postFound.updateImages(currentImages);
    }

    public void deletePostFound(PostFound postFound, Member member){

        boolean isOwner = member.equals(postFound.getMember());

        boolean isAdmin = member.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new GeneralException(ErrorCode.UNAUTHORIZED_DELETING);
        }

        if(!postFoundRepository.existsById(postFound.getId())){
            throw new GeneralException(ErrorCode.POST_NOT_FOUND);
        }
        postFoundRepository.delete(postFound);
    }

    /**
     * PostFound의 DogStatus만 업데이트
     */
    public PostFound updatePostFoundStatus(PostFound postFound, Member member, DogStatus dogStatus) {
        
        // TODO: 권한 확인 - 본인만 상태 변경 가능
        // if (!member.equals(postFound.getMember())) {
        //     throw new GeneralException(ErrorCode.UNAUTHORIZED_UPDATING);
        // }

        // 상태 업데이트
        postFound.updateStatus(dogStatus);
        
        return postFound;
    }

}

