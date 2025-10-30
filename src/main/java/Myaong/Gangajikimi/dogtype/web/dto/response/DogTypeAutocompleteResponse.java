package Myaong.Gangajikimi.dogtype.web.dto.response;

import Myaong.Gangajikimi.dogtype.entity.DogType;

public class DogTypeAutocompleteResponse {
    
    /**
     * DB에서 조회한 DogType 엔티티로부터 견종명 반환
     */
    public static String from(DogType dogType) {
        return dogType.getType();
    }
}