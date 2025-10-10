package Myaong.Gangajikimi.postfound.web.controller;

import Myaong.Gangajikimi.auth.userDetails.CustomUserDetails;
import Myaong.Gangajikimi.common.dto.request.DogStatusUpdateRequest;
import Myaong.Gangajikimi.common.dto.request.FilterRequest;
import Myaong.Gangajikimi.common.dto.response.PageResponse;
import Myaong.Gangajikimi.common.enums.SortType;
import Myaong.Gangajikimi.common.enums.TimeFilter;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import Myaong.Gangajikimi.facade.PostFoundFacade;
import Myaong.Gangajikimi.postfound.web.docs.PostFoundControllerDocs;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundUpdateRequest;
import Myaong.Gangajikimi.postfoundreport.dto.PostFoundReportRequest;
import Myaong.Gangajikimi.postfoundreport.dto.PostFoundReportResponse;
import Myaong.Gangajikimi.postfound.web.dto.request.PostFoundRequest;
import Myaong.Gangajikimi.postfound.web.dto.response.PostFoundDetailResponse;
import Myaong.Gangajikimi.postfound.service.PostFoundQueryService;

import Myaong.Gangajikimi.postfoundreport.service.PostFoundReportService;
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
@RequestMapping("/api/found-posts")
public class PostFoundController implements PostFoundControllerDocs {

    private final PostFoundFacade postFoundFacade;
    private final PostFoundReportService postFoundReportService;
    private final PostFoundQueryService postFoundQueryService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<GlobalResponse> postFound(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws JsonProcessingException {

        Long memberId = userDetails.getId();
        
        PostFoundRequest request = objectMapper.readValue(dataJson, PostFoundRequest.class);

        return GlobalResponse.onSuccess(SuccessCode.OK, postFoundFacade.postPostFound(request, memberId, images));
    }

    @PatchMapping(value = "/{postFoundId}", consumes = "multipart/form-data")
    public ResponseEntity<GlobalResponse> updateFound(@RequestPart("data") String dataJson,
                                                     @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                     @PathVariable Long postFoundId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) throws JsonProcessingException {

        Long memberId = userDetails.getId();
        
        PostFoundUpdateRequest request = objectMapper.readValue(dataJson, PostFoundUpdateRequest.class);

        return GlobalResponse.onSuccess(SuccessCode.OK,
                postFoundFacade.updatePostFound(request, memberId, postFoundId, images));
    }

    @DeleteMapping("/{postFoundId}")
    public ResponseEntity<GlobalResponse> deleteFound(@PathVariable Long postFoundId,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getId();

        postFoundFacade.deletePostFound(memberId, postFoundId);

        return GlobalResponse.onSuccess(SuccessCode.OK);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getFoundPosts(
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

        PageResponse response = postFoundQueryService.getFoundPosts(size, page, request);
        
        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<GlobalResponse> getMyFoundPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long memberId = userDetails.getId();
        PageResponse response = postFoundQueryService.getMyFoundPosts(memberId, page, size);
        
        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @GetMapping("/{postFoundId}")
    public ResponseEntity<GlobalResponse> getPostFoundDetail(@PathVariable Long postFoundId) {

        PostFoundDetailResponse response = postFoundFacade.getPostFoundDetail(postFoundId);

        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @PostMapping("/{postFoundId}/reports")
    public ResponseEntity<GlobalResponse> reportPostFound(@PathVariable Long postFoundId,
                                                          @RequestBody PostFoundReportRequest request,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getId();
        PostFoundReportResponse response = postFoundReportService.reportPostFound(postFoundId, request, memberId);

        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }

    @PatchMapping("/{postFoundId}/status")
    public ResponseEntity<GlobalResponse> updatePostFoundStatus(
            @PathVariable Long postFoundId,
            @RequestBody DogStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getId();
        var response = postFoundFacade.updatePostFoundStatus(postFoundId, request, memberId);

        return GlobalResponse.onSuccess(SuccessCode.OK, response);
    }
}

