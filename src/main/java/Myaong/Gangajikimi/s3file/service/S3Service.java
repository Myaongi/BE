package Myaong.Gangajikimi.s3file.service;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private final S3Presigner s3Presigner;
    private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "gif", "svg");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String upload(MultipartFile image, String keyPrefix, String fileName) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new GeneralException(ErrorCode.EMPTY_FILE_EXCEPTION);
        }
        
        // 파일 크기 체크 (사용자 업로드 파일 크기 로그 출력)
        long fileSize = image.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            log.error("파일 크기 초과: {}MB (제한: 5MB)", String.format("%.2f", fileSize / 1024.0 / 1024.0));
            throw new GeneralException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
        
        return this.uploadImage(image, keyPrefix, fileName);
    }

    private String uploadImage(MultipartFile image, String keyPrefix, String fileName) {
        this.validateExtension(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image, keyPrefix, fileName);
        } catch (IOException e) {
            throw new GeneralException(ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    private String uploadImageToS3(MultipartFile image, String keyPrefix, String fileName) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new GeneralException(ErrorCode.EMPTY_FILE_EXCEPTION);
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new GeneralException(ErrorCode.NO_FILE_EXTENSION);
        }

        String extension = getExtension(originalFilename);
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "_" + fileName + "." + extension; // 유니크 파일명
        String keyName = keyPrefix + "/" + s3FileName;

        // AWS SDK v2 방식으로 변경
        software.amazon.awssdk.core.sync.RequestBody requestBody = 
            software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                image.getInputStream(), image.getSize());

        try {
            System.out.println("Uploading image to S3: " + s3FileName);

            // S3 업로드 실행
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType("image/" + extension)
                    .build();

            // 실제 S3 업로드 실행
            s3Client.putObject(objectRequest, requestBody);
            
            System.out.println("S3 업로드 성공: " + s3FileName);
        } catch (Exception e) {
            System.err.println("S3 업로드 실패: " + e.getMessage());
            e.printStackTrace();
            throw new GeneralException(ErrorCode.PUT_OBJECT_EXCEPTION);
        }

        return keyName;
    }

	// 게시글 작성 시에는 Presigned URL을 사용하지 않고 서버에서 직접 업로드

	/**
	 * DB에 저장된 keyName으로부터 다운로드용 Presigned URL 생성
	 * @param keyName DB에 저장된 S3 객체 키
	 * @return 다운로드 가능한 Presigned URL (keyName이 null이면 null 반환)
	 */
	public String generatePresignedUrl(String keyName) {
		// keyName이 null이거나 빈 문자열이면 null 반환
		if (keyName == null || keyName.isBlank()) {
			log.info("[S3Service] keyName이 null이거나 빈 문자열입니다. null 반환");
			return null;
		}
		
		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(keyName)
				.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(60)) // 1시간 유효
				.getObjectRequest(getObjectRequest)
				.build();

			URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
			String result = presignedUrl.toString();
			log.info("[S3Service] PresignedURL 생성 완료 - keyName: {}", keyName);
			return result;
		} catch (Exception e) {
			log.error("[S3Service] PresignedURL 생성 실패 - keyName: {}, error: {}", keyName, e.getMessage());
			// 예외 발생 시 null 반환 (에러 대신 null로 처리)
			return null;
		}
	}

	/**
	 * 여러 개의 keyName에 대해 다운로드용 Presigned URL 목록 생성
	 * @param keyNames DB에 저장된 S3 객체 키 목록
	 * @return 다운로드 가능한 Presigned URL 목록 (null 제외)
	 */
	public List<String> generatePresignedUrls(List<String> keyNames) {
		// keyNames가 null이면 빈 리스트 반환
		if (keyNames == null || keyNames.isEmpty()) {
			log.info("[S3Service] keyNames가 null이거나 빈 리스트입니다. 빈 리스트 반환");
			return List.of();
		}
		
		log.info("[S3Service] PresignedURL 목록 생성 시작 - keyNames 수: {}", keyNames.size());
		
		List<String> presignedUrls = keyNames.stream()
			.map(this::generatePresignedUrl)
			.filter(Objects::nonNull) // null인 URL 제외
			.toList();
			
		log.info("[S3Service] PresignedURL 목록 생성 완료 - 생성된 URL 수: {}", presignedUrls.size());
		return presignedUrls;
	}

    public String extractKeyFromUrl(String presignedUrl) {
        log.info("추출하려는 Presigned URL: {}", presignedUrl);
        
        try {
            // 간단한 URL 파싱 방식
            java.net.URI uri = java.net.URI.create(presignedUrl);
            String path = uri.getPath();
            
            // 경로에서 첫 번째 '/' 제거
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            log.info("추출된 S3 키: {}", path);
            return path;
        } catch (Exception e) {
            log.error("S3 키 추출 실패: {}", presignedUrl, e);
            throw new GeneralException(ErrorCode.INVALID_S3_URL);
        }
    }

	/**
	 * S3에서 파일 삭제
	 * @param keyName 삭제할 파일의 keyName
	 */
	public void deleteFile(String keyName) {
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(keyName)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (Exception e) {
			throw new GeneralException(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
		}
	}

	// 확장자 유효성 검사
	private void validateExtension(String fileName) {
		String ext = getExtension(fileName).toLowerCase();
		if (!ALLOWED_EXTENSIONS.contains(ext)) {
			throw new IllegalArgumentException("허용되지 않은 파일 확장자: " + ext);
		}
	}


	private String getExtension(String fileName) {
		int dotIdx = fileName.lastIndexOf(".");
		if (dotIdx == -1) {
			throw new IllegalArgumentException("파일 확장자가 없습니다.");
		}
		return fileName.substring(dotIdx + 1);
	}
}
