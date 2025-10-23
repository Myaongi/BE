package Myaong.Gangajikimi.facade;

import Myaong.Gangajikimi.common.dto.request.DogStatusUpdateRequest;
import Myaong.Gangajikimi.common.dto.response.DogStatusUpdateResponse;
import Myaong.Gangajikimi.member.entity.Member;
import Myaong.Gangajikimi.member.service.MemberService;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.service.PostFoundCommandService;

import Myaong.Gangajikimi.postfound.service.PostFoundQueryService;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundRequest;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundUpdateRequest;
import Myaong.Gangajikimi.postfound.web.dto.response.PostFoundResponse;
import Myaong.Gangajikimi.postfound.web.dto.response.PostFoundDetailResponse;
import Myaong.Gangajikimi.templocation.service.TempLocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFoundFacade {

    private final MemberService memberService;
    private final PostFoundCommandService postFoundCommandService;
    private final PostFoundQueryService postFoundQueryService;
    private final TempLocationService tempLocationService;

    @Transactional
    public PostFoundResponse postPostFound(PostFoundRequest request,
                                           Long memberId,
                                           List<MultipartFile> images,
                                           MultipartFile aiImage) {

        // Member 생성
        Member member = memberService.findMemberById(memberId);

        // TODO: 생성된 AI 이미지 추가

        // 게시글 생성
        PostFound postFound = postFoundCommandService.postPostFound(request, member, images, aiImage);

        // 임시 좌표 저장
        tempLocationService.saveTempLocation(request.getFoundLongitude(), request.getFoundLatitude(), postFound);

        return PostFoundResponse.of(postFound.getId(), member.getMemberName(), postFound.getTitle(), postFound.getCreatedAt(), postFound.getStatus());
    }

    @Transactional
    public PostFoundResponse updatePostFound(PostFoundUpdateRequest request,
                                             Long memberId,
                                             Long postFoundId,
                                             List<MultipartFile> images){

        // Member 조회
        Member member = memberService.findMemberById(memberId);

        // 게시글 조회
        PostFound postFound = postFoundQueryService.findPostFoundById(postFoundId);

        // 관련된 tempLocationService 값이 변경될 경우 수정
        tempLocationService.updateTempLocation(request.getFoundLongitude(), request.getFoundLatitude(), postFound);

        //업데이트 후 결과 반환
        return PostFoundResponse.from(postFoundCommandService.updatePostFound(request, member, postFound, images));
    }

    @Transactional
    public void deletePostFound(Long memberId, Long postFoundId){

        Member member = memberService.findMemberById(memberId);

        PostFound postFound = postFoundQueryService.findPostFoundById(postFoundId);

        postFoundCommandService.deletePostFound(postFound, member);
    }

    public PostFoundDetailResponse getPostFoundDetail(Long postFoundId) {

        return postFoundQueryService.getPostFoundDetail(postFoundId);
    }

    @Transactional
    public DogStatusUpdateResponse updatePostFoundStatus(Long postFoundId, DogStatusUpdateRequest request, Long memberId) {
        
        // Member 조회
        Member member = memberService.findMemberById(memberId);
        
        // 게시글 조회
        PostFound postFound = postFoundQueryService.findPostFoundById(postFoundId);
        
        // 상태 업데이트
        PostFound updatedPostFound = postFoundCommandService.updatePostFoundStatus(postFound, member, request.getDogStatus());
        
        return DogStatusUpdateResponse.of(
            updatedPostFound.getId(), 
            updatedPostFound.getStatus(), 
            updatedPostFound.getUpdatedAt()
        );
    }

}

