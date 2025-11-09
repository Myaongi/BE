package Myaong.Gangajikimi.postfound.service;

import Myaong.Gangajikimi.common.dto.response.DogStatusUpdateResponse;
import Myaong.Gangajikimi.common.enums.DogGender;
import Myaong.Gangajikimi.common.enums.DogStatus;
import Myaong.Gangajikimi.common.enums.PostType;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.dogtype.service.DogTypeService;
import Myaong.Gangajikimi.common.enums.Role;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.ai.service.AiService;
import Myaong.Gangajikimi.ai.web.dto.response.EmbeddingResponse;
import Myaong.Gangajikimi.matchingpost.service.MatchingPostService;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.notification.service.NotificationService;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundRequest;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundUpdateRequest;
import Myaong.Gangajikimi.postfoundembedding.service.PostFoundEmbeddingService;
import Myaong.Gangajikimi.s3file.service.S3Service;
import Myaong.Gangajikimi.kakaoapi.service.KakaoApiService;
import org.springframework.transaction.annotation.Transactional;
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
    private final AiService aiService;
    private final PostFoundEmbeddingService postFoundEmbeddingService;
    private final MatchingPostService matchingPostService;

    public PostFound postPostFound(PostFoundRequest request,
                                   Member member,
                                   List<MultipartFile> images,
                                   MultipartFile aiImage) {

        long startTime = System.currentTimeMillis();
        log.info("[PostFound 작성 시작] Member : {}", member.getMemberName());

        // 실제 이미지와 AI 이미지 중 하나는 무조건 들어옴 (프론트엔드에서 보장)
        boolean hasRealImages = (images != null && !images.isEmpty()) &&
                images.stream().anyMatch(img -> img != null && !img.isEmpty() && img.getSize() > 0);
        boolean hasAiImage = (aiImage != null && !aiImage.isEmpty() && aiImage.getSize() > 0);
        
        log.info("[이미지 검증] hasRealImages: {}, hasAiImage: {}, images size: {}, aiImage size: {}",
                hasRealImages, hasAiImage,
                (images != null ? images.size() : 0),
                (aiImage != null ? aiImage.getSize() : 0));

        String processedDogType = request.getDogType();

        // DogType 엔티티 조회 (처리된 견종 정보로)
        DogType dogType = dogTypeService.findByTypeName(processedDogType);
        // 성별 정보 변환
        DogGender dogGender;
        try {
            dogGender = DogGender.valueOf(request.getDogGender().toUpperCase());
        } catch (Exception e) {
            // 한글 '모름', 그 외 잘못된 값 모두 NEUTRAL로 처리
            dogGender = DogGender.NEUTRAL;
        }

        // 발견 위치 Point 객체 생성
        Point newPoint = geometryFactory.createPoint(
                new Coordinate(request.getFoundLongitude(), request.getFoundLatitude())
        );

        // 좌표를 주소로 변환 (카카오 API 사용)
        String foundRegion = kakaoApiService.getAddrFromKakaoApi(
                request.getFoundLongitude(),
                request.getFoundLatitude()
        );
        log.info("[Kakao API 호출 완료] 실행 시간: {}ms", System.currentTimeMillis() - startTime);

        // PostFound 엔티티 생성 (이미지는 나중에 업데이트)
        PostFound newPostFound = PostFound.of(
                null, // 이미지는 S3 업로드 후 설정
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
                null,
                null
        );

        // DB에 저장하여 ID 생성
        PostFound savedPostFound = postFoundRepository.save(newPostFound);
        log.info("[게시글 저장 완료] PostFound ID: {}, 실행 시간: {}ms", savedPostFound.getId(), System.currentTimeMillis() - startTime);

        // S3에 이미지 업로드 및 keyName 목록 생성
        List<String> imageKeyNames;
        String aiImageKeyName;

        // 실제 이미지 또는 AI 이미지 중 하나는 무조건 존재
        if (hasAiImage) {
            // AI 이미지 업로드
            aiImageKeyName = s3Service.upload(
                    aiImage,
                    "postFound",
                    savedPostFound.getId().toString() + "_ai" // postId_ai 형태로 저장
            );
            log.info("[AI 이미지 업로드 완료] AI 이미지 keyName: {}", aiImageKeyName);

            savedPostFound.setAiImage(aiImageKeyName);

            // AI 이미지 임베딩 생성 및 저장
            generateAndSaveEmbedding(savedPostFound,
                    aiImage,
                    processedDogType,
                    request.getDogColor(),
                    request.getFeatures(),
                    "AI 이미지",
                    startTime);

        } else if (hasRealImages) {
            // 실제 이미지들 업로드
            imageKeyNames = images.stream()
                    .filter(image -> image != null && !image.isEmpty()) // null 또는 빈 파일 필터링
                    .map(image -> s3Service.upload(
                            image,
                            "postFound",
                            savedPostFound.getId().toString()) // 폴더/게시글ID/파일명 형태로 저장
                    )
                    .toList();
            log.info("[실제 이미지 업로드 완료] 업로드된 이미지 수: {}, 실행 시간: {}ms", imageKeyNames.size(), System.currentTimeMillis() - startTime);

            // 업로드된 이미지 keyNames로 PostFound 업데이트
            savedPostFound.updateImages(imageKeyNames);

            // 실제 이미지 임베딩 생성 및 저장
            generateAndSaveEmbedding(savedPostFound,
                    images.get(0),
                    processedDogType,
                    request.getDogColor(),
                    request.getFeatures(),
                    "실제 이미지",
                    startTime);

        } else {
            // 이 경우는 발생하지 않아야 함 (프론트엔드에서 보장)
            log.error("[이미지 검증 실패] images: {}, aiImage: {}, images null: {}, aiImage null: {}",
                    (images != null ? images.size() : "null"),
                    (aiImage != null ? aiImage.getSize() : "null"),
                    (images == null),
                    (aiImage == null));
            throw new GeneralException(ErrorCode.NO_IMAGE);
        }

        // 반경 3km 이내 유저들에게 실시간 알림 전송
        log.info("[알림 전송 시작] 반경 3km 이내 유저에게 알림");
        notificationService.notifyNearbyUsers(
            savedPostFound.getId(),
            request.getFoundLatitude(),
            request.getFoundLongitude(),
            member.getId(),
            PostType.FOUND
        );

        long endTime = System.currentTimeMillis();
        log.info("[PostFound 작성 완료] PostFound ID: {}, 실행 시간: {}ms",
                savedPostFound.getId(), (endTime - startTime));

        return savedPostFound;
    }

    public PostFound updatePostFound(PostFoundUpdateRequest request,
                                     Member member,
                                     PostFound postFound,
                                     List<MultipartFile> images,
                                     MultipartFile aiImage){

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
            if (postFound.getAiImage() != null && !postFound.getAiImage().isEmpty()) {
                s3Service.deleteFile(postFound.getAiImage());
                log.info("[기존 AI 이미지 삭제] keyName: {}", postFound.getAiImage());
            }

            // 새 AI 이미지 업로드
            aiImageKeyName = s3Service.upload(
                    aiImage,
                    "postLost",
                    postFound.getId().toString() + "_ai"
            );
            log.info("[새 AI 이미지 업로드 완료] keyName: {}", aiImageKeyName);
        }

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

        // 게시글의 실제 이미지들 삭제
        if (postFound.getRealImage() != null && !postFound.getRealImage().isEmpty()) {
            postFound.getRealImage().forEach(s3Service::deleteFile);
            log.info("[게시글 삭제] 실제 이미지 삭제 완료");
        }

        // 게시글의 AI 이미지 삭제
        if (postFound.getAiImage() != null && !postFound.getAiImage().isEmpty()) {
            s3Service.deleteFile(postFound.getAiImage());
            log.info("[게시글 삭제] AI 이미지 삭제 완료");
        }

        postFoundRepository.delete(postFound);
    }

    /**
     * 여러 PostFound 게시글의 DogStatus를 일괄 업데이트
     */
    @Transactional
    public List<DogStatusUpdateResponse> updatePostFoundStatuses(
            List<Long> postFoundIds, Member member, DogStatus dogStatus) {
        
        List<DogStatusUpdateResponse> responses = new ArrayList<>();
        
        for (Long postFoundId : postFoundIds) {
            PostFound postFound = postFoundRepository.findById(postFoundId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

            // 상태 업데이트
            postFound.updateStatus(dogStatus);
            
            // 상태가 RETURNED로 변경되면 해당 게시글의 모든 MatchingPost 삭제
            if (dogStatus == DogStatus.RETURNED) {
                matchingPostService.deleteAllByPostFound(postFound);
            }
            
            responses.add(DogStatusUpdateResponse.of(
                postFound.getId(),
                postFound.getStatus(),
                postFound.getUpdatedAt()
            ));
        }
        
        return responses;
    }

    /**
     * 이미지와 텍스트로 임베딩을 생성하고 저장하는 메서드
     */
    private void generateAndSaveEmbedding(PostFound postFound, 
                                        MultipartFile image, 
                                        String dogType, 
                                        String dogColor, 
                                        String features, 
                                        String imageType,
                                        long startTime) {
        log.info("[임베딩 생성 시작] {} 사용", imageType);
        
        EmbeddingResponse embeddingResponse = aiService.generateEmbedding(
                image,
                dogType,
                dogColor,
                features
        );
        log.info("[FastAPI 임베딩 생성 요청 완료] {} 사용, 실행 시간: {}ms", imageType, System.currentTimeMillis() - startTime);

        // 임베딩 생성 성공 시 DB에 저장
        if (embeddingResponse != null) {
            postFoundEmbeddingService.saveEmbedding(
                    postFound,
                    embeddingResponse.imageEmbeddingToArray(), // 이미지 임베딩 벡터
                    embeddingResponse.textEmbeddingToArray()// 텍스트 임베딩 벡터
            );
            postFound.updateDogInfo(String.join("\n", embeddingResponse.getSentences()));
            log.info("[{} 임베딩 저장 완료]", imageType);
        } else {
            log.warn("[{} 임베딩 생성 실패] embeddingResponse가 null입니다.", imageType);
        }
    }

}

