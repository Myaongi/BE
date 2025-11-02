package Myaong.Gangajikimi.postlost.web;


import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.dto.request.DogStatusUpdateRequest;
import Myaong.Gangajikimi.common.dto.request.FilterRequest;
import Myaong.Gangajikimi.common.dto.response.PageResponse;
import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.facade.PostLostFacade;
import Myaong.Gangajikimi.postlost.web.docs.PostLostControllerDocs;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostUpdateSpotsRequest;
import Myaong.Gangajikimi.postlostreport.service.PostLostReportService;
import Myaong.Gangajikimi.postlostreport.dto.PostLostReportRequest;
import Myaong.Gangajikimi.postlost.web.dto.request.PostLostRequest;
import Myaong.Gangajikimi.postlost.service.PostLostQueryService;

import Myaong.Gangajikimi.postlost.web.dto.response.PostLostDetailResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lost-posts")
public class PostLostController implements PostLostControllerDocs {

    private final PostLostFacade postLostFacade;
    private final PostLostReportService postLostReportService;
    private final PostLostQueryService postLostQueryService;
    private final ObjectMapper objectMapper;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<GlobalResponse> postLost(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "aiImage", required = false) MultipartFile aiImage,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws JsonProcessingException {

        Long memberId = userDetails.getId();
        
        PostLostRequest request = objectMapper.readValue(dataJson, PostLostRequest.class);

        return GlobalResponse.onSuccess(SuccessCode.OK,
                postLostFacade.postPostLost(request, memberId, images, aiImage));
    }

    @PatchMapping(value = "/{postLostId}", consumes = "multipart/form-data")
    public ResponseEntity<GlobalResponse> updateLost(@RequestPart("data") String dataJson,
                                                     @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                     @RequestPart(value = "aiImage", required = false) MultipartFile aiImage,
                                                     @PathVariable Long postLostId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) throws JsonProcessingException {

        Long memberId = userDetails.getId();
        
        PostLostUpdateRequest request = objectMapper.readValue(dataJson, PostLostUpdateRequest.class);


        return GlobalResponse.onSuccess(SuccessCode.OK,
                postLostFacade.updatePostLost(request, memberId, postLostId, images, aiImage));
    }

    @DeleteMapping("/{postLostId}")
    public ResponseEntity<GlobalResponse> deleteLost(@PathVariable Long postLostId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getId();

        postLostFacade.deletePostLost(memberId, postLostId);

        return GlobalResponse.onSuccess(SuccessCode.OK);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getLostPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "LATEST") SortType sortType,
            @RequestParam(required = false) Integer maxDistance,
            @RequestParam(required = false) TimeFilter timeFilter,
            @RequestParam(required = false) Double userLongitude,
            @RequestParam(required = false) Double userLatitude) {
        
        FilterRequest request = FilterRequest.builder()
                .sortType(sortType)
                .maxDistance(maxDistance)
                .timeFilter(timeFilter)
                .userLongitude(userLongitude)
                .userLatitude(userLatitude)
                .build();
        
        PageResponse response = postLostQueryService.getLostPosts(page, size, request);
        
        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<GlobalResponse> getMyLostPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        Long memberId = userDetails.getId();

        PageResponse response = postLostQueryService.getMyLostPosts(memberId, page, size);
        
        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @GetMapping("/{postLostId}")
    public ResponseEntity<GlobalResponse> getPostLostDetail(@PathVariable Long postLostId) {
        PostLostDetailResponse response = postLostFacade.getPostLostDetail(postLostId);
        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping("/{postLostId}/reports")
    public ResponseEntity<GlobalResponse> reportPostLost(@PathVariable Long postLostId,
                                                         @RequestBody PostLostReportRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        Long memberId = userDetails.getId();
        var response = postLostReportService.reportPostLost(postLostId, request, memberId);
        
        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @PatchMapping("/{postLostId}/status")
    public ResponseEntity<GlobalResponse> updatePostLostStatus(
            @PathVariable Long postLostId,
            @RequestBody DogStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getId();
        var response = postLostFacade.updatePostLostStatus(postLostId, request, memberId);

        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping("/{postLostId}/spots")
    public ResponseEntity<GlobalResponse> updatePostLostSpots(@PathVariable Long postLostId,
                                                              @RequestBody PostLostUpdateSpotsRequest request) {

        postLostFacade.updatePostLostLocation(postLostId, request);

        return GlobalResponse.onSuccess(SuccessCode.OK);
    }

}
