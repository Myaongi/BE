package Myaong.Gangajikimi.postlost.service;

import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.common.util.TimeUtil;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.postlost.web.dto.response.PostLostDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLostQueryService {

    private final PostLostRepository postLostRepository;

    public PostLost findPostLostById(Long postId) {

        PostLost postLost = postLostRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        return postLost;

    }

    public PostLostDetailResponse getPostLostDetail(Long postId) {
        PostLost postLost = postLostRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));

        return PostLostDetailResponse.of(
                postLost.getId(),
                postLost.getTitle(),
                postLost.getDogName(),
                postLost.getDogType(),
                postLost.getDogColor(),
                postLost.getDogGender(),
                postLost.getContent(),
                postLost.getLostDate(),
                postLost.getLostTime(),
                postLost.getLostSpot().getX(), // longitude
                postLost.getLostSpot().getY(), // latitude
                // TODO: 주소 변환 API 연동 후 활성화 (예: "서울시 송파구")
                // getAddressFromCoordinates(postLost.getLostSpot().getX(), postLost.getLostSpot().getY()),
                // TODO: Cloud 스토리지 연동 후 활성화
                // postLost.getAiImage(),
                // postLost.getRealImage(),
                postLost.getMember().getMemberName(),
                postLost.getCreatedAt(),
                TimeUtil.getTimeAgo(postLost.getCreatedAt())
        );
    }

}
