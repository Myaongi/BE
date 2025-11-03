package Myaong.Gangajikimi.facade;

import Myaong.Gangajikimi.common.dto.response.DogStatusUpdateResponse;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostDogStatusUpdateRequest;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.member.service.MemberService;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.service.PostLostCommandService;
import Myaong.Gangajikimi.postlost.service.PostLostQueryService;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateSpotsRequest;
import Myaong.Gangajikimi.postlost.web.dto.response.PostLostPostResponse;
import Myaong.Gangajikimi.postlost.web.dto.response.PostLostDetailResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLostFacade {

    private final MemberService memberService;
    private final PostLostCommandService postLostCommandService;
    private final PostLostQueryService postLostQueryService;

    @Transactional
    public PostLostPostResponse postPostLost(PostLostRequest request,
                                             Long memberId,
                                             List<MultipartFile> images,
                                             MultipartFile aiImage){

        // Member 생성
        Member member = memberService.findMemberById(memberId);

        // TODO: 생성된 AI 이미지 추가

        // 게시글 생성
        PostLost postLost = postLostCommandService.postPostLost(request, member, images, aiImage);

        // DB 저장

        return PostLostPostResponse.of(postLost.getId(), member.getMemberName(), postLost.getTitle(), postLost.getCreatedAt(), postLost.getStatus());
    }

    @Transactional
    public PostLostPostResponse updatePostLost(PostLostUpdateRequest request,
                                               Long memberId,
                                               Long postLostId,
                                               List<MultipartFile> images,
                                               MultipartFile aiImage){

        // Member 조회
        Member member = memberService.findMemberById(memberId);

        // 게시글 조회
        PostLost postLost = postLostQueryService.findPostLostById(postLostId);

        //업데이트 후 결과 반환
        return PostLostPostResponse.from(postLostCommandService.updatePostLost(request, member, postLost, images, aiImage));
    }

    @Transactional
    public void deletePostLost(Long memberId, Long postLostId){

        Member member = memberService.findMemberById(memberId);

        PostLost postLost = postLostQueryService.findPostLostById(postLostId);

        postLostCommandService.deletePostLost(postLost, member);
    }

    public PostLostDetailResponse getPostLostDetail(Long postLostId) {
        return postLostQueryService.getPostLostDetail(postLostId);
    }

    @Transactional
    public List<DogStatusUpdateResponse> updatePostLostStatuses(PostLostDogStatusUpdateRequest request, Long memberId) {
        
        // Member 조회
        Member member = memberService.findMemberById(memberId);
        
        // 여러 게시글 상태 일괄 업데이트
        return postLostCommandService.updatePostLostStatuses(request.getPostLostIds(), member, request.getDogStatus());
    }

    public void updatePostLostLocation(Long postLostId, PostLostUpdateSpotsRequest request){

        postLostCommandService.updatePostLostLocation(postLostId, request);

    }

}
