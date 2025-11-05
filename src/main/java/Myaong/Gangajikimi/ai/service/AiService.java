package Myaong.Gangajikimi.ai.service;

import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.dogtype.dto.response.DogBreedResponse;
import Myaong.Gangajikimi.ai.web.dto.request.SimilarityScoreRequest;
import Myaong.Gangajikimi.ai.web.dto.request.DogInfoRequest;
import Myaong.Gangajikimi.ai.web.dto.response.EmbeddingResponse;
import Myaong.Gangajikimi.ai.web.dto.response.SimilarityScoreResponse;
import Myaong.Gangajikimi.ai.web.dto.response.TextNormalizeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    private final RestTemplate restTemplate; // AppConfig에 rootUri가 설정된 Bean

    /**
     * 강아지 이미지를 FastAPI 서버로 전송하여 품종 예측 결과를 문자열로 받아옵니다.
     *
     * @param imageFile Controller에서 받은 강아지 이미지 파일
     * @return FastAPI 서버가 예측한 강아지 품종 텍스트 (예: "골든 리트리버")
     * @throws IOException 이미지 파일 처리 중 발생할 수 있는 예외
     */
    public String analyzeImage(MultipartFile imageFile) throws IOException {

        final String apiPath = fastApiBaseUrl + "/api/v1/dogbreed/";

        // 이미지를 처리할 타입
        ByteArrayResource resource = null;

        // 1. HTTP Header 설정 (multipart/form-data)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 2. Multipart Body 생성
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

        // MultipartFile에서 직접 바이트와 파일명을 가져와 ByteArrayResource로 만든다.
        try {
            resource = new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new GeneralException(ErrorCode.IO_ERROR_FOR_IMAGE);
        }

        // FastAPI 코드: image: UploadFile = File(...) -> 'image' key 사용
        // FastAPI의 'image' 파라미터 이름과 일치해야 함
        requestBody.add("image", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<DogBreedResponse> responseEntity = restTemplate.postForEntity(
                apiPath,
                requestEntity,
                DogBreedResponse.class // 응답은 DogBreedResponseDto로 매핑
        );

        // 5. 응답받은 DTO에서 결과(품종 텍스트)를 추출하여 반환합니다.
        DogBreedResponse responseDto = responseEntity.getBody();
        if (responseDto != null) {
            return responseDto.getResult();
        } else {
            // 서버 응답이 비어있는 경우 예외 처리
            throw new GeneralException(ErrorCode.AI_SERVER_ERROR);
        }
    }

    /**
     * 강아지 정보를 Gemini AI로 정제하여 3개의 문장으로 변환합니다.
     * 실패 시 빈 문자열을 반환합니다.
     *
     * @param breed 강아지 품종
     * @param colors 강아지 색상
     * @param features 강아지 특징
     * @return 정제된 텍스트 (3개 문장을 '\n'로 join), 실패 시 빈 문자열
     */
    public String normalizeText(String breed, String colors, String features) {

        final String apiPath = fastApiBaseUrl + "/api/v1/embed/normalize";

        try {
            // 1. HTTP Header 설정 (application/json)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Request Body 생성
            DogInfoRequest requestBody = DogInfoRequest.builder()
                    .breed(breed)
                    .colors(colors)
                    .features(features != null ? features : "")
                    .build();

            HttpEntity<DogInfoRequest> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<TextNormalizeResponse> responseEntity = restTemplate.postForEntity(
                    apiPath,
                    requestEntity,
                    TextNormalizeResponse.class
            );

            TextNormalizeResponse response = responseEntity.getBody();
            if (response != null && response.getSentences() != null && !response.getSentences().isEmpty()) {
                String processedText = String.join("\n", response.getSentences());
                log.info("텍스트 정제 성공 - breed: {}, sentences: {}", breed, response.getSentences().size());
                return processedText;
            } else {
                log.warn("FastAPI 텍스트 정제 응답이 비어있습니다 - breed: {}, colors: {}", breed, colors);
                return "";
            }
        } catch (Exception e) {
            log.error("FastAPI 텍스트 정제 요청 실패 - breed: {}, error: {}", breed, e.getMessage());
            return "";
        }
    }

    /**
     * 강아지 이미지와 텍스트를 임베딩으로 변환합니다.
     * 실패 시 null을 반환합니다.
     *
     * @param imageFile 강아지 이미지 파일
     * @param breed 강아지 품종
     * @param colors 강아지 색상
     * @param features 강아지 특징
     * @return 이미지 임베딩, 텍스트 임베딩, 정제된 문장들 (실패 시 null)
     */
    public EmbeddingResponse generateEmbedding(
            MultipartFile imageFile,
            String breed,
            String colors,
            String features
    ) {

        final String apiPath = fastApiBaseUrl + "/api/v1/embed";

        try {
            log.info("FastAPI 임베딩 생성 요청 - URL: {}, breed: {}", apiPath, breed);

            // 1. HTTP Header 설정 (multipart/form-data)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 2. Multipart Body 생성
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();

            // 이미지 파일 추가
            ByteArrayResource imageResource = new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            };
            requestBody.add("image", imageResource);

            // 텍스트 필드 추가
            requestBody.add("breed", breed);
            requestBody.add("colors", colors);
            requestBody.add("features", features != null ? features : "");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<EmbeddingResponse> responseEntity = restTemplate.postForEntity(
                    apiPath,
                    requestEntity,
                    EmbeddingResponse.class
            );

            EmbeddingResponse response = responseEntity.getBody();
            if (response != null) {
                log.info("임베딩 생성 성공 - breed: {}, imageSize: {}, textSize: {}",
                        breed,
                        response.getImage() != null ? response.getImage().size() : 0,
                        response.getText() != null ? response.getText().size() : 0);
                return response;
            } else {
                log.warn("FastAPI 임베딩 생성 응답이 비어있습니다 - breed: {}", breed);
                return null;
            }
        } catch (IOException e) {
            log.error("이미지 파일 읽기 실패 - breed: {}, error: {}", breed, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("FastAPI 임베딩 생성 요청 실패 - breed: {}, error: {}", breed, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 두 강아지 게시물의 임베딩 간 유사도를 계산합니다. (float[] 버전)
     * Entity에서 float[] 타입으로 저장된 임베딩을 직접 사용할 수 있습니다.
     * 가중치는 FastAPI 서버의 config.py에 설정된 값을 사용합니다.
     *
     * @param embAImage 게시물 A의 이미지 임베딩 (float[])
     * @param embAText 게시물 A의 텍스트 임베딩 (float[])
     * @param embBImage 게시물 B의 이미지 임베딩 (float[])
     * @param embBText 게시물 B의 텍스트 임베딩 (float[])
     * @return 유사도 계산 결과 (0.0 ~ 1.0)
     */
    public SimilarityScoreResponse calculateSimilarity(
            float[] embAImage,
            float[] embAText,
            float[] embBImage,
            float[] embBText
    ) {

        final String apiPath = fastApiBaseUrl + "/api/v1/similarity/score";

        // 1. HTTP Header 설정 (application/json)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Request Body 생성 (float[] -> List<Float> 변환)
        SimilarityScoreRequest requestBody = SimilarityScoreRequest.of(
                embAImage,
                embAText,
                embBImage,
                embBText
        );

        HttpEntity<SimilarityScoreRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<SimilarityScoreResponse> responseEntity = restTemplate.postForEntity(
                    apiPath,
                    requestEntity,
                    SimilarityScoreResponse.class
            );

            SimilarityScoreResponse response = responseEntity.getBody();
            if (response != null) {
                return response;
            } else {
                log.error("FastAPI 유사도 계산 응답이 비어있습니다.");
                throw new GeneralException(ErrorCode.AI_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("FastAPI 유사도 계산 요청 실패: {}", e.getMessage());
            throw new GeneralException(ErrorCode.AI_SERVER_ERROR);
        }
    }

    /**
     * 강아지 정보를 바탕으로 AI 이미지를 생성합니다.
     * 이미지 바이너리를 Content-Type: image/png 헤더와 함께 반환합니다.
     * 
     * @param request 강아지 정보 요청 (품종, 색상, 특징)
     * @return 이미지 바이너리와 헤더를 포함한 ResponseEntity, 실패 시 500 에러 응답
     */
    public ResponseEntity<byte[]> generateDogImage(DogInfoRequest request) {

        final String apiPath = fastApiBaseUrl + "/api/v1/imagegen";

        String breed = request.getBreed();
        String colors = request.getColors();
        String features = request.getFeatures();

        try {
            log.info("AI 이미지 생성 요청 - URL: {}, breed: {}, colors: {}", apiPath, breed, colors);

            // 1. HTTP Header 설정 (application/json)
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            // 2. Request Body 생성 (DogInfoRequest 재사용)
            DogInfoRequest requestBody = DogInfoRequest.builder()
                    .breed(breed != null ? breed : "")
                    .colors(colors != null ? colors : "")
                    .features(features != null ? features : "")
                    .build();

            HttpEntity<DogInfoRequest> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

            // 3. 이미지 바이너리로 응답 받기
            ResponseEntity<byte[]> responseEntity = restTemplate.postForEntity(
                    apiPath,
                    requestEntity,
                    byte[].class
            );

            byte[] imageBytes = responseEntity.getBody();
            if (imageBytes != null && imageBytes.length > 0) {
                log.info("이미지 생성 성공 - breed: {}, imageSize: {} bytes", breed, imageBytes.length);
                
                // 4. 이미지 응답을 위한 헤더 설정
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.IMAGE_PNG);
                responseHeaders.setContentLength(imageBytes.length);
                
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(imageBytes);
            } else {
                log.warn("FastAPI 이미지 생성 응답이 비어있습니다 - breed: {}, colors: {}", breed, colors);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (RestClientException e) {
            log.error("FastAPI 이미지 생성 요청 실패 - breed: {}, error: {}", breed, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("FastAPI 이미지 생성 중 예상치 못한 오류 발생 - breed: {}, error: {}", breed, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}