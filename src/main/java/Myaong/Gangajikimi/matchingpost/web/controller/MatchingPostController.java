package Myaong.Gangajikimi.matchingpost.web.controller;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.matchingpost.service.MatchingPostService;
import Myaong.Gangajikimi.matchingpost.web.docs.MatchingPostControllerDocs;
import Myaong.Gangajikimi.matchingpost.web.dto.request.MatchingPostRequest;
import Myaong.Gangajikimi.matchingpost.web.dto.response.MatchingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/matchings")
public class MatchingPostController implements MatchingPostControllerDocs {

    private final MatchingPostService matchingPostService;

    /**
     * 게시글 ID를 받아서 매칭 가능한 게시글들을 탐색하는 API
     * @param request 매칭 요청 (게시글 ID와 타입)
     * @return 매칭 결과 (게시글 ID, 제목, 매칭된 게시글 개수)
     */
    @PostMapping
    public ResponseEntity<GlobalResponse> matchingPosts(@RequestBody MatchingPostRequest request) {

        MatchingResponse results = matchingPostService.findMatchingPosts(request);

        return GlobalResponse.onSuccess(SuccessCode.OK, results);
    }

    @GetMapping("/postLost/{postLostId}")
    public ResponseEntity<GlobalResponse> matchingPostLostList(@RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "20") Integer size,
                                                               @PathVariable Long postLostId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        return GlobalResponse.onSuccess(
                SuccessCode.OK,
                matchingPostService.getMatchingPostsByLost(postLostId, page, size, userDetails.getId())
        );

    }

    @GetMapping("/postFound/{postFoundId}")
    public ResponseEntity<GlobalResponse> matchingPostFoundList(@RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "20") Integer size,
                                                                @PathVariable Long postFoundId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails){
        return GlobalResponse.onSuccess(
                SuccessCode.OK,
                matchingPostService.getMatchingPostsByFound(postFoundId, page, size, userDetails.getId())
        );
    }

    @DeleteMapping("/{matchingId}")
    public ResponseEntity<GlobalResponse> deleteMatching(@PathVariable Long matchingId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails){

        matchingPostService.deleteMatchingPost(matchingId, userDetails.getId());

        return GlobalResponse.onSuccess(SuccessCode.OK);

    }

}
