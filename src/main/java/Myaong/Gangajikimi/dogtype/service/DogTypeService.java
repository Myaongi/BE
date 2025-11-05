package Myaong.Gangajikimi.dogtype.service;

import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.dogtype.entity.DogType;
import Myaong.Gangajikimi.dogtype.repository.DogTypeRepository;
import Myaong.Gangajikimi.dogtype.web.dto.response.DogTypeAutocompleteResponse;
import Myaong.Gangajikimi.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DogTypeService {
    
    private final DogTypeRepository dogTypeRepository;
    private final AiService aiService;
    
    /**
     * 견종 이름으로 DogType 엔티티 조회
     * @param typeName 견종 이름
     * @return DogType 엔티티
     * @throws GeneralException 견종을 찾을 수 없는 경우
     */
    public DogType findByTypeName(String typeName) {
        return dogTypeRepository.findByType(typeName)
                .orElseThrow(() -> new GeneralException(ErrorCode.DOG_TYPE_NOT_FOUND));
    }
    
    /**
     * 견종 ID로 DogType 엔티티 조회
     * @param id 견종 ID
     * @return DogType 엔티티
     * @throws GeneralException 견종을 찾을 수 없는 경우
     */
    public DogType findById(Long id) {
        return dogTypeRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorCode.DOG_TYPE_NOT_FOUND));
    }
    
    /**
     * 견종 자동완성 기능 (한글 기반)
     * @param keyword 검색 키워드 (최소 2글자)
     * @return 매칭되는 견종명 목록, 없으면 빈 리스트
     */
    public List<String> searchDogTypes(String keyword) {
        if (keyword == null || keyword.length() < 2) {
            return Collections.emptyList();
        }
        
        // DB에서 검색
        List<DogType> dbResults = dogTypeRepository.findByTypeContainingIgnoreCase(keyword);
        
        // DB 결과를 견종명으로 변환
        return dbResults.stream()
                .map(DogTypeAutocompleteResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 모든 견종 목록 반환
     * @return 전체 견종명 목록
     */
    public List<String> getAllDogTypes() {
        // DB에서 모든 견종 조회

        List<DogType> results = dogTypeRepository.findAll();
        
        return results.stream()
                .map(DogTypeAutocompleteResponse::from)
                .collect(Collectors.toList());
    }

    public String getDogBreed(MultipartFile dogImage) {

        try{
            return aiService.analyzeImage(dogImage);
        } catch (IOException e) {
            throw new GeneralException(ErrorCode.DOG_TYPE_NOT_FOUND);
        }

    }

}
