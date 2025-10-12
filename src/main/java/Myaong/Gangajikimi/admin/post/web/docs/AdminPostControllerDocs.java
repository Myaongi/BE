package Myaong.Gangajikimi.admin.post.web.docs;

import Myaong.Gangajikimi.common.response.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "AdminPost", description = "관리자: 게시물 관리 API")
public interface AdminPostControllerDocs {

    @Operation(summary = "게시물 목록 조회", description = "type=ALL/FOUND/LOST, aiOnly=true/false, page/size 페이징")
    ResponseEntity<GlobalResponse> list(
            @RequestParam String type,
            @RequestParam boolean aiOnly,
            @RequestParam int page,
            @RequestParam int size
    );

    @Operation(summary = "게시물 상세 보기", description = "type=FOUND/LOST + postId로 상세조회")
    ResponseEntity<GlobalResponse> detail(
            @PathVariable String type,
            @PathVariable Long postId
    );

    @Operation(summary = "게시물 삭제", description = "type=FOUND/LOST + postId 삭제")
    ResponseEntity<GlobalResponse> delete(
            @PathVariable String type,
            @PathVariable Long postId
    );
}