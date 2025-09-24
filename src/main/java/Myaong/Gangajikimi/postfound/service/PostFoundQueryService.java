package Myaong.Gangajikimi.postfound.service;

import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.common.util.TimeUtil;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postfound.web.dto.response.PostFoundDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostFoundQueryService {

    private final PostFoundRepository postFoundRepository;

    public PostFound findPostFoundById(Long postId) {

        PostFound postFound = postFoundRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        return postFound;

    }

    public PostFoundDetailResponse getPostFoundDetail(Long postId) {
        PostFound postFound = postFoundRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        return PostFoundDetailResponse.of(
                postFound.getId(),
                postFound.getTitle(),
                null, // PostFound에는 dogName이 없음
                postFound.getDogType(),
                postFound.getDogColor(),
                postFound.getDogGender(),
                postFound.getContent(),
                postFound.getFoundDate(),
                postFound.getFoundTime(),
                postFound.getFoundSpot().getX(), // longitude
                postFound.getFoundSpot().getY(), // latitude
                // TODO: 주소 변환 API 연동 후 활성화 (예: "서울시 송파구")
                // getAddressFromCoordinates(postFound.getFoundSpot().getX(), postFound.getFoundSpot().getY()),
                // TODO: Cloud 스토리지 연동 후 활성화
                // postFound.getAiImage(),
                // postFound.getRealImage(),
                postFound.getMember().getMemberName(),
                postFound.getCreatedAt(),
                TimeUtil.getTimeAgo(postFound.getCreatedAt())
        );
    }

}
