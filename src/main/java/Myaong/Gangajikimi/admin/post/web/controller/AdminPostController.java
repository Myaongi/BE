package Myaong.Gangajikimi.admin.post.web.controller;

import Myaong.Gangajikimi.admin.post.service.AdminPostService;
import Myaong.Gangajikimi.admin.post.web.docs.AdminPostControllerDocs;
import Myaong.Gangajikimi.common.response.GlobalResponse;
import Myaong.Gangajikimi.common.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 - 게시물 관리 컨트롤러
 * 관리자 권한(ROLE_ADMIN)만 접근 가능
 */
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController implements AdminPostControllerDocs {

    private final AdminPostService service;

    /**
     * 게시물 목록 조회 (전체/FOUND/LOST + AI 이미지 여부 + 페이지네이션)
     */
    @GetMapping
    @Override
    public ResponseEntity<GlobalResponse> list(
            @RequestParam(defaultValue = "ALL") String type,     // ALL | FOUND | LOST
            @RequestParam(defaultValue = "false") boolean aiOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var result = service.getPosts(type, aiOnly, PageRequest.of(page, size));
        return GlobalResponse.onSuccess(SuccessCode.OK, result);
    }

    /**
     * 게시물 상세 보기
     */
    @GetMapping("/{type}/{postId}")
    @Override
    public ResponseEntity<GlobalResponse> detail(
            @PathVariable String type,   // FOUND | LOST
            @PathVariable Long postId
    ) {
        var result = service.getDetail(type, postId);
        return GlobalResponse.onSuccess(SuccessCode.OK, result);
    }

    /**
     * 게시물 삭제
     */
    @DeleteMapping("/{type}/{postId}")
    @Override
    public ResponseEntity<GlobalResponse> delete(
            @PathVariable String type,   // FOUND | LOST
            @PathVariable Long postId
    ) {
        service.delete(type, postId);
        return GlobalResponse.onSuccess(SuccessCode.OK, "게시물이 삭제되었습니다.");
    }
}