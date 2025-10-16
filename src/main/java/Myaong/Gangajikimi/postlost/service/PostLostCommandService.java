package Myaong.Gangajikimi.postlost.service;

import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.dogtype.service.DogTypeService;
import Myaong.Gangajikimi.common.enums.Role;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.fastapi.dto.response.EmbeddingResponse;
import Myaong.Gangajikimi.fastapi.service.FastApiService;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.notification.service.NotificationService;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateRequest;
import Myaong.Gangajikimi.postlostembedding.service.PostLostEmbeddingService;
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
public class PostLostCommandService {

    private final PostLostRepository postLostRepository;
    private final DogTypeService dogTypeService;
    private final S3Service s3Service;
    private final KakaoApiService kakaoApiService;
    private final NotificationService notificationService;
    private final GeometryFactory geometryFactory;
    private final FastApiService fastApiService;
    private final PostLostEmbeddingService postLostEmbeddingService;

    public PostLost postPostLost(PostLostRequest request, Member member, List<MultipartFile> images){
        long startTime = System.currentTimeMillis();
        log.info("[PostLost 작성 시작] Member : {}", member.getMemberName());

        DogType dogType = dogTypeService.findByTypeName(request.getDogType());
        DogGender dogGender = DogGender.valueOf(request.getDogGender());

        Point newPoint = geometryFactory.createPoint(new Coordinate(request.getLostLongitude(), request.getLostLatitude()));

        String lostRegion = kakaoApiService.getAddrFromKakaoApi(request.getLostLongitude(), request.getLostLatitude());

        // FastAPI로 정제된 텍스트 가져오기 (실패 시 빈 문자열)
        String dogInfo = fastApiService.normalizeText(
                request.getDogType(),
                request.getDogColor(),
                request.getFeatures()
        );

        // 먼저 PostLost를 저장해서 ID를 얻음
        PostLost newPostLost = PostLost.of(null, // 이미지는 나중에 설정
                member,
                request.getTitle(),
                request.getDogName(),
                dogType,
                dogGender,
                request.getDogColor(),
                request.getFeatures(),
                newPoint,
                request.getLostDate(),
                request.getLostTime(),
                lostRegion,
                dogInfo);

        PostLost savedPostLost = postLostRepository.save(newPostLost);

        // 이미지 업로드 및 keyName 목록 생성 (stream 사용)
        List<String> imageKeyNames = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            imageKeyNames = images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .map(image -> s3Service.upload(image, "postLost", savedPostLost.getId().toString()))
                    .toList();
        }

        // 업로드된 이미지 keyNames로 PostLost 업데이트
        savedPostLost.updateImages(imageKeyNames);

        // 텍스트, 이미지 임베딩 값 생성 및 저장
        if (images != null && !images.isEmpty()) {
            EmbeddingResponse embeddingResponse = fastApiService.generateEmbedding(
                    images.get(0),
                    request.getDogType(),
                    request.getDogColor(),
                    request.getFeatures()
            );
            
            if (embeddingResponse != null) {
                postLostEmbeddingService.saveEmbedding(
                        savedPostLost,
                        embeddingResponse.imageEmbeddingToArray(),
                        embeddingResponse.textEmbeddingToArray()
                );
            }
        }

        /**
         * 반경 3km 유저들에게 알림 전송
         * */
        notificationService.notifyNearbyUsers(
            savedPostLost.getId(),
            request.getLostLatitude(),
            request.getLostLongitude(),
            member.getId(),
            PostType.LOST
        );

        long endTime = System.currentTimeMillis();
        log.info("[PostLost 작성 완료] PostLost ID: {}, 실행 시간: {}ms", savedPostLost.getId(), (endTime - startTime));

        return savedPostLost;
    }

    public PostLost updatePostLost(PostLostUpdateRequest request, Member member, PostLost postLost, List<MultipartFile> images){

        // 권한 확인
        if(!member.equals(postLost.getMember())){
            throw new GeneralException(ErrorCode.UNAUTHORIZED_UPDATING);
        }

        // 세 가지 중 하나라도 수정되었다면 true
        boolean isDogInfoChanged =
                !(postLost.getDogType().getType().equals(request.getDogType()) &&
                postLost.getDogColor().equals(request.getDogColor()) &&
                postLost.getContent().equals(request.getFeatures()));

        Point point = geometryFactory.createPoint(new Coordinate(request.getLostLongitude(), request.getLostLatitude()));

        String lostRegion = kakaoApiService.getAddrFromKakaoApi(request.getLostLongitude(), request.getLostLatitude());

        String dogInfo = postLost.getDogInfo();

        // 강아지 정보가 변경되었으면 다시 생성
        if(isDogInfoChanged){
            dogInfo = fastApiService.normalizeText(
                    request.getDogType(),
                    request.getDogColor(),
                    request.getFeatures()
            );
        }

        // 1. 삭제할 이미지 처리
        List<String> deletedImageKeys = new ArrayList<>();
        if (request.getDeletedImageUrls() != null && !request.getDeletedImageUrls().isEmpty()) {
            // 삭제할 이미지들의 키 추출
            deletedImageKeys = request.getDeletedImageUrls().stream()
                    .map(s3Service::extractKeyFromUrl)
                    .toList();

            // S3에서 파일 삭제
            deletedImageKeys.forEach(s3Service::deleteFile);
        }

        // 2. 새 이미지 업로드
        List<String> newImageKeyNames = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            newImageKeyNames = images.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .map(image -> s3Service.upload(image, "postLost", postLost.getId().toString()))
                    .toList();
        }
        
        // 3. 순서를 보장하는 이미지 업데이트
        updateImagesWithOrder(postLost, deletedImageKeys, newImageKeyNames);
        
        // 4. 게시글 정보 업데이트 (이미지 제외)
        DogType dogType = dogTypeService.findByTypeName(request.getDogType());
        postLost.update(request, point, dogType, lostRegion, dogInfo);

        return postLost;
    }

    /**
     * 순서를 보장하는 이미지 업데이트
     * 삭제된 이미지의 자리에 뒤의 이미지들이 앞으로 이동하고, 새 이미지는 맨 뒤에 추가
     */
    private void updateImagesWithOrder(PostLost postLost, List<String> deletedImageKeys, List<String> newImageKeyNames) {
        List<String> currentImages = new ArrayList<>(postLost.getRealImage());
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
        postLost.updateImages(currentImages);
    }

    public void deletePostLost(PostLost postLost, Member member){

        boolean isOwner = member.equals(postLost.getMember());

        boolean isAdmin = member.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new GeneralException(ErrorCode.UNAUTHORIZED_DELETING);
        }

        if(!postLostRepository.existsById(postLost.getId())){
            throw new GeneralException(ErrorCode.POST_NOT_FOUND);
        }

        postLostRepository.delete(postLost);
    }

    /**
     * PostLost의 DogStatus만 업데이트
     */
    public PostLost updatePostLostStatus(PostLost postLost, Member member, DogStatus dogStatus) {
        
        // TODO: 권한 확인 - 본인만 상태 변경 가능
        // if (!member.equals(postLost.getMember())) {
        //     throw new GeneralException(ErrorCode.UNAUTHORIZED_UPDATING);
        // }

        // 상태 업데이트
        postLost.updateStatus(dogStatus);
        
        return postLost;
    }


}
