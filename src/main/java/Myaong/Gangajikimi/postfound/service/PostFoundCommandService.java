package Myaong.Gangajikimi.postfound.service;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.dogtype.service.DogTypeService;
import Myaong.Gangajikimi.common.enums.Role;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.fastapi.service.FastApiService;
import Myaong.Gangajikimi.fastapi.dto.response.EmbeddingResponse;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.notification.service.NotificationService;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundRequest;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundUpdateRequest;
import Myaong.Gangajikimi.postfoundembedding.service.PostFoundEmbeddingService;
import Myaong.Gangajikimi.s3file.service.S3Service;
import Myaong.Gangajikimi.kakaoapi.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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
    private final FastApiService fastApiService;
    private final PostFoundEmbeddingService postFoundEmbeddingService;

    public PostFound postPostFound(PostFoundRequest request, Member member, List<MultipartFile> images){

        DogType dogType = dogTypeService.findByTypeName(request.getDogType());
        DogGender dogGender = DogGender.valueOf(request.getDogGender());

        Point newPoint = geometryFactory.createPoint(new Coordinate(request.getFoundLongitude(), request.getFoundLatitude()));

        String foundRegion = kakaoApiService.getAddrFromKakaoApi(request.getFoundLongitude(), request.getFoundLatitude());

        // FastAPI로 정제된 텍스트 가져오기 (실패 시 빈 문자열)
        String dogInfo = fastApiService.normalizeText(
                request.getDogType(),
                request.getDogColor(),
                request.getFeatures()
        );

        /*
        TODO : 사용자가 견종 정보도 모르고 이미지도 null일 경우
            사용자가 견종도 모르고 사진도 못 찍은 경우
            우리가 견종을 만들어주려면 이미지를 받아야 함.
            우리가 AI 이미지를 만들어주려면 견종 정보를 받아야 함.
         */

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
                foundRegion,
                dogInfo);

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

        // 텍스트, 이미지 임베딩 값 생성 및 저장
        if (images != null && !images.isEmpty()) {
            EmbeddingResponse embeddingResponse = fastApiService.generateEmbedding(
                    images.get(0),
                    request.getDogType(),
                    request.getDogColor(),
                    request.getFeatures()
            );
            
            if (embeddingResponse != null) {
                postFoundEmbeddingService.saveEmbedding(
                        savedPostFound,
                        embeddingResponse.imageEmbeddingToArray(),
                        embeddingResponse.textEmbeddingToArray()
                );
            }
        }

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

        // 세 가지 중 하나라도 수정되었다면 false, 모두 같으면 true (변수명과 로직이 반대였음)
        boolean isDogInfoChanged =
                !(postFound.getDogType().getType().equals(request.getDogType()) &&
                postFound.getDogColor().equals(request.getDogColor()) &&
                postFound.getContent().equals(request.getFeatures()));

        Point point = geometryFactory.createPoint(new Coordinate(request.getFoundLongitude(), request.getFoundLatitude()));

        String foundRegion = kakaoApiService.getAddrFromKakaoApi(request.getFoundLongitude(), request.getFoundLatitude());

        String dogInfo = postFound.getDogInfo();

        // 강아지 정보가 변경되었으면 다시 생성
        if(isDogInfoChanged){
            dogInfo = fastApiService.normalizeText(
                    request.getDogType(),
                    request.getDogColor(),
                    request.getFeatures()
            );
        }

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
        postFound.update(request, point, dogType, foundRegion, dogInfo);
        


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

        boolean isAdmin = member.getRole() == Role.ADMIN;

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
        
        // 권한 확인 - 본인만 상태 변경 가능
        //if (!member.equals(postFound.getMember())) {
        //    throw new GeneralException(ErrorCode.UNAUTHORIZED_UPDATING);
        //}

        // 상태 업데이트
        postFound.updateStatus(dogStatus);
        
        return postFound;
    }

}

