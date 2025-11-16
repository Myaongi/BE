package Myaong.Gangajikimi.postlost.service;

import Myaong.Gangajikimi.common.dto.response.DogStatusUpdateResponse;
import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.dogtype.service.DogTypeService;
import Myaong.Gangajikimi.common.enums.Role;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.ai.web.dto.response.EmbeddingResponse;
import Myaong.Gangajikimi.ai.service.AiService;
import Myaong.Gangajikimi.fixedlocation.entity.FixedLocation;
import Myaong.Gangajikimi.fixedlocation.service.FixedLocationService;
import Myaong.Gangajikimi.matchingpost.service.MatchingPostService;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.notification.service.NotificationService;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateSpotsRequest;
import Myaong.Gangajikimi.postlostembedding.service.PostLostEmbeddingService;
import Myaong.Gangajikimi.s3file.service.S3Service;
import Myaong.Gangajikimi.kakaoapi.service.KakaoApiService;
import jakarta.transaction.Transactional;
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
    private final AiService aiService;
    private final PostLostEmbeddingService postLostEmbeddingService;
    private final FixedLocationService fixedLocationService;
    private final MatchingPostService matchingPostService;

    @Transactional
    public PostLost postPostLost(PostLostRequest request, Member member, List<MultipartFile> images, MultipartFile aiImage){
        long startTime = System.currentTimeMillis();
        log.info("[PostLost 작성 시작] Member : {}", member.getMemberName());

        // 실제 이미지와 AI 이미지 중 하나는 무조건 들어옴 (프론트엔드에서 보장)
        boolean hasRealImages = (images != null && !images.isEmpty());
        boolean hasAiImage = (aiImage != null && !aiImage.isEmpty());
        
        log.info("[이미지 초기 체크] images null: {}, images empty: {}, images size: {}, aiImage null: {}, aiImage empty: {}", 
                images == null, 
                images != null && images.isEmpty(),
                images != null ? images.size() : 0,
                aiImage == null, 
                aiImage != null && aiImage.isEmpty());
        
        if (aiImage != null) {
            log.info("[AI 이미지 상세 정보] originalFilename: {}, size: {}, contentType: {}, isEmpty: {}", 
                    aiImage.getOriginalFilename(), 
                    aiImage.getSize(), 
                    aiImage.getContentType(),
                    aiImage.isEmpty());
        }
        
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile img = images.get(i);
                log.info("[실제 이미지 {}] originalFilename: {}, size: {}, contentType: {}, isEmpty: {}", 
                        i, 
                        img.getOriginalFilename(), 
                        img.getSize(), 
                        img.getContentType(),
                        img.isEmpty());
            }
        }

        String processedDogType = request.getDogType();

        DogType dogTypeEntity = dogTypeService.findByTypeName(processedDogType);
        DogGender dogGender = DogGender.valueOf(request.getDogGender());

        Point newPoint = geometryFactory.createPoint(new Coordinate(request.getLostLongitude(), request.getLostLatitude()));

        String lostRegion = kakaoApiService.getAddrFromKakaoApi(request.getLostLongitude(), request.getLostLatitude());
        log.info("[Kakao API 호출 완료] 실행 시간: {}ms", System.currentTimeMillis() - startTime);

        // 먼저 PostLost를 저장해서 ID를 얻음
        PostLost newPostLost = PostLost.of(null, // 이미지는 나중에 설정
                member,
                request.getTitle(),
                request.getDogName(),
                dogTypeEntity,
                dogGender,
                request.getDogColor(),
                request.getFeatures(),
                newPoint,
                request.getLostDate(),
                request.getLostTime(),
                lostRegion,
                null);

        PostLost savedPostLost = postLostRepository.save(newPostLost);
        log.info("[게시글 저장 완료] PostLost ID: {}, 실행 시간: {}ms", savedPostLost.getId(), System.currentTimeMillis() - startTime);

        // S3에 이미지 업로드 및 keyName 목록 생성
        List<String> imageKeyNames;
        String aiImageKeyName;

        // 이미지 분기 체크 로그
        log.info("[이미지 분기 체크 시작] PostLost ID: {}, hasRealImages: {}, hasAiImage: {}", 
                savedPostLost.getId(), hasRealImages, hasAiImage);

        // 실제 이미지 또는 AI 이미지 중 하나는 무조건 존재
        if (hasAiImage) {
            log.info("[AI 이미지 분기 진입] PostLost ID: {}", savedPostLost.getId());
            try {
                log.info("[AI 이미지 S3 업로드 시작] PostLost ID: {}, size: {}", savedPostLost.getId(), aiImage.getSize());
                // AI 이미지 업로드
                aiImageKeyName = s3Service.upload(
                        aiImage,
                        "postLost",
                        savedPostLost.getId().toString() + "_ai" // postId_ai 형태로 저장
                );
                log.info("[AI 이미지 업로드 완료] AI 이미지 keyName: {}, 실행 시간: {}ms", 
                        aiImageKeyName, System.currentTimeMillis() - startTime);
                
                savedPostLost.setAiImage(aiImageKeyName);
                
                // AI 이미지 임베딩 생성 및 저장
                log.info("[AI 이미지 임베딩 생성 시작] PostLost ID: {}", savedPostLost.getId());
                generateAndSaveEmbedding(savedPostLost,
                        aiImage,
                        processedDogType,
                        request.getDogColor(),
                        request.getFeatures(),
                        "AI 이미지",
                        startTime);
                log.info("[AI 이미지 임베딩 생성 완료] PostLost ID: {}, 실행 시간: {}ms", 
                        savedPostLost.getId(), System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("[AI 이미지 처리 실패] PostLost ID: {}, error: {}", 
                        savedPostLost.getId(), e.getMessage(), e);
                throw e;
            }
                    
        } else if (hasRealImages) {
            log.info("[실제 이미지 분기 진입] PostLost ID: {}, images count: {}", savedPostLost.getId(), images.size());
            // 실제 이미지들 업로드
            imageKeyNames = images.stream()
                    .filter(image -> image != null && !image.isEmpty()) // null 또는 빈 파일 필터링
                    .map(image -> s3Service.upload(
                            image,
                            "postLost",
                            savedPostLost.getId().toString()) // 폴더/게시글ID/파일명 형태로 저장
                    )
                    .toList();
            log.info("[실제 이미지 업로드 완료] 업로드된 이미지 수: {}, 실행 시간: {}ms", imageKeyNames.size(), System.currentTimeMillis() - startTime);

            // 업로드된 이미지 keyNames로 PostLost 업데이트
            savedPostLost.updateImages(imageKeyNames);
            
            // 실제 이미지 임베딩 생성 및 저장
            generateAndSaveEmbedding(savedPostLost,
                    images.get(0),
                    processedDogType,
                    request.getDogColor(),
                    request.getFeatures(),
                    "실제 이미지",
                    startTime);
                    
        } else {
            // 이 경우는 발생하지 않아야 함 (프론트엔드에서 보장)
            log.error("[이미지 없음 예외 발생] PostLost ID: {}, hasRealImages: {}, hasAiImage: {}, images null: {}, aiImage null: {}", 
                    savedPostLost.getId(), hasRealImages, hasAiImage, images == null, aiImage == null);
            throw new GeneralException(ErrorCode.NO_IMAGE);
        }


        /**
         * 반경 3km 유저들에게 알림 전송
         * */
        log.info("[알림 전송 시작] PostLost ID: {}, latitude: {}, longitude: {}", 
                savedPostLost.getId(), request.getLostLatitude(), request.getLostLongitude());
        try {
            notificationService.notifyNearbyUsers(
                savedPostLost.getId(),
                request.getLostLatitude(),
                request.getLostLongitude(),
                member.getId(),
                PostType.LOST
            );
            log.info("[알림 전송 완료] PostLost ID: {}, 실행 시간: {}ms", 
                    savedPostLost.getId(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("[알림 전송 실패] PostLost ID: {}, error: {}", 
                    savedPostLost.getId(), e.getMessage(), e);
            // 알림 실패해도 게시글 작성은 성공으로 처리
        }

        long endTime = System.currentTimeMillis();
        log.info("[PostLost 작성 완료] PostLost ID: {}, 전체 실행 시간: {}ms", savedPostLost.getId(), (endTime - startTime));

        return savedPostLost;
    }

    @Transactional
    public PostLost updatePostLost(PostLostUpdateRequest request,
                                   Member member,
                                   PostLost postLost,
                                   List<MultipartFile> images,
                                   MultipartFile aiImage){

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
            dogInfo = aiService.normalizeText(
                    request.getDogType(),
                    request.getDogColor(),
                    request.getFeatures()
            );
        }

        // AI 이미지 업데이트 처리
        String aiImageKeyName = null;
        if (aiImage != null && !aiImage.isEmpty()) {
            // 기존 AI 이미지가 있다면 삭제
            if (postLost.getAiImage() != null && !postLost.getAiImage().isEmpty()) {
                s3Service.deleteFile(postLost.getAiImage());
                log.info("[기존 AI 이미지 삭제] keyName: {}", postLost.getAiImage());
            }
            
            // 새 AI 이미지 업로드
            aiImageKeyName = s3Service.upload(
                    aiImage,
                    "postLost",
                    postLost.getId().toString() + "_ai"
            );
            log.info("[새 AI 이미지 업로드 완료] keyName: {}", aiImageKeyName);
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
        
        // 5. AI 이미지가 있으면 PostLost에 AI 이미지 설정
        if (aiImageKeyName != null) {
            postLost.setAiImage(aiImageKeyName);
            log.info("[AI 이미지 설정 완료] PostLost에 AI 이미지 keyName 설정: {}", aiImageKeyName);
        }

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

        // 게시글의 실제 이미지들 삭제
        if (postLost.getRealImage() != null && !postLost.getRealImage().isEmpty()) {
            postLost.getRealImage().forEach(s3Service::deleteFile);
            log.info("[게시글 삭제] 실제 이미지 삭제 완료");
        }

        // 게시글의 AI 이미지 삭제
        if (postLost.getAiImage() != null && !postLost.getAiImage().isEmpty()) {
            s3Service.deleteFile(postLost.getAiImage());
            log.info("[게시글 삭제] AI 이미지 삭제 완료");
        }

        postLostRepository.delete(postLost);
    }

    /**
     * 여러 PostLost 게시글의 DogStatus를 일괄 업데이트
     */
    @Transactional
    public List<DogStatusUpdateResponse> updatePostLostStatuses(
            List<Long> postLostIds, Member member, DogStatus dogStatus) {
        
        List<DogStatusUpdateResponse> responses = new ArrayList<>();
        
        for (Long postLostId : postLostIds) {
            PostLost postLost = postLostRepository.findById(postLostId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));
            
            // 상태 업데이트
            postLost.updateStatus(dogStatus);
            
            // 상태가 RETURNED로 변경되면 해당 게시글의 모든 MatchingPost 삭제
            if (dogStatus == DogStatus.RETURNED) {
                matchingPostService.deleteAllByPostLost(postLost);
            }
            
            responses.add(DogStatusUpdateResponse.of(
                postLost.getId(),
                postLost.getStatus(),
                postLost.getUpdatedAt()
            ));
        }
        
        return responses;
    }

    /**
     * 이미지와 텍스트로 임베딩을 생성하고 저장하는 메서드
     */
    private void generateAndSaveEmbedding(PostLost postLost, 
                                        MultipartFile image, 
                                        String dogType, 
                                        String dogColor, 
                                        String features, 
                                        String imageType,
                                        long startTime) {
        log.info("[임베딩 생성 시작] {} 사용, PostLost ID: {}, image size: {}, contentType: {}, originalFilename: {}", 
                imageType, postLost.getId(), image.getSize(), image.getContentType(), image.getOriginalFilename());
        
        try {
            log.info("[FastAPI 임베딩 생성 요청 시작] PostLost ID: {}, dogType: {}, dogColor: {}", 
                    postLost.getId(), dogType, dogColor);
            
            EmbeddingResponse embeddingResponse = aiService.generateEmbedding(
                    image,
                    dogType,
                    dogColor,
                    features
            );
            log.info("[FastAPI 임베딩 생성 요청 완료] {} 사용, 실행 시간: {}ms", imageType, System.currentTimeMillis() - startTime);

            // 임베딩 생성 성공 시 DB에 저장
            if (embeddingResponse != null) {
                log.info("[임베딩 저장 시작] PostLost ID: {}, imageEmbedding size: {}, textEmbedding size: {}", 
                        postLost.getId(),
                        embeddingResponse.getImage() != null ? embeddingResponse.getImage().size() : 0,
                        embeddingResponse.getText() != null ? embeddingResponse.getText().size() : 0);
                
                postLostEmbeddingService.saveEmbedding(
                        postLost,
                        embeddingResponse.imageEmbeddingToArray(), // 이미지 임베딩 벡터
                        embeddingResponse.textEmbeddingToArray()   // 텍스트 임베딩 벡터
                );
                postLost.updateDogInfo(String.join("\n", embeddingResponse.getSentences()));
                log.info("[{} 임베딩 저장 완료] PostLost ID: {}, 실행 시간: {}ms", 
                        imageType, postLost.getId(), System.currentTimeMillis() - startTime);
            } else {
                log.warn("[{} 임베딩 생성 실패] embeddingResponse가 null입니다. PostLost ID: {}", imageType, postLost.getId());
            }
        } catch (Exception e) {
            log.error("[{} 임베딩 생성 중 예외 발생] PostLost ID: {}, error: {}", 
                    imageType, postLost.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updatePostLostLocation(Long postLostId, PostLostUpdateSpotsRequest request){

        PostLost postLost = postLostRepository.findById(postLostId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        Point newPoint = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));

        fixedLocationService.save(FixedLocation.of(postLost, newPoint));
    }

}
