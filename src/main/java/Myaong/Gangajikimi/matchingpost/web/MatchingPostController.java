package Myaong.Gangajikimi.matchingpost.web;

import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.matchingpost.entity.MatchingPost;
import Myaong.Gangajikimi.matchingpost.service.MatchingPostService;
import Myaong.Gangajikimi.matchingpost.web.dto.MatchingPostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matchings")
public class MatchingPostController {

    private final MatchingPostService matchingPostService;

    @PostMapping
    ResponseEntity<?> matchingPost(@RequestBody MatchingPostRequest request){


        return GlobalResponse.onSuccess(SuccessCode.OK);
    }


}
