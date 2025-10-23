package Myaong.Gangajikimi.admin.post.service;

import Myaong.Gangajikimi.admin.post.web.dto.AdminPostDto;
import Myaong.Gangajikimi.common.exception.GeneralException;
import Myaong.Gangajikimi.common.response.ErrorCode;
import Myaong.Gangajikimi.postfound.entity.PostFound;
import Myaong.Gangajikimi.postfound.repository.PostFoundRepository;
import Myaong.Gangajikimi.postlost.entity.PostLost;
import Myaong.Gangajikimi.postlost.repository.PostLostRepository;
import Myaong.Gangajikimi.s3file.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPostService {

    private final PostLostRepository lostRepo;
    private final PostFoundRepository foundRepo;
    private final S3Service s3Service;

    /**
     * 게시물 목록 조회 (type=ALL|FOUND|LOST, aiOnly, page/size)
     * 정렬은 레포지토리 메서드의 OrderByCreatedAtDesc로 처리.
     */
    public AdminPostDto.PageResponse<AdminPostDto.ListItem> getPosts(
            String type, boolean aiOnly, Pageable pageable
    ) {
        if ("LOST".equalsIgnoreCase(type)) {
            Page<PostLost> page = aiOnly
                    ? lostRepo.findByDeletedByAdminFalseAndAiImageIsNotNullOrderByCreatedAtDesc(pageable)
                    : lostRepo.findByDeletedByAdminFalseOrderByCreatedAtDesc(pageable);
            return toPage(page.map(this::toListItemLost));
        }

        if ("FOUND".equalsIgnoreCase(type)) {
            Page<PostFound> page = aiOnly
                    ? foundRepo.findByDeletedByAdminFalseAndAiImageIsNotNullOrderByCreatedAtDesc(pageable)
                    : foundRepo.findByDeletedByAdminFalseOrderByCreatedAtDesc(pageable);
            return toPage(page.map(this::toListItemFound));
        }

        // type = ALL: 간단 병합 (각각 size*2만큼 가져와 최신순 상위 size 반환)
        int want = pageable.getPageSize();
        Pageable doubled = PageRequest.of(pageable.getPageNumber(), want * 2);

        Page<PostLost>  lostPage  = aiOnly
                ? lostRepo.findByDeletedByAdminFalseAndAiImageIsNotNullOrderByCreatedAtDesc(doubled)
                : lostRepo.findByDeletedByAdminFalseOrderByCreatedAtDesc(doubled);

        Page<PostFound> foundPage = aiOnly
                ? foundRepo.findByDeletedByAdminFalseAndAiImageIsNotNullOrderByCreatedAtDesc(doubled)
                : foundRepo.findByDeletedByAdminFalseOrderByCreatedAtDesc(doubled);

        List<AdminPostDto.ListItem> merged = new ArrayList<>(lostPage.getNumberOfElements() + foundPage.getNumberOfElements());
        lostPage.getContent().forEach(e -> merged.add(toListItemLost(e)));
        foundPage.getContent().forEach(e -> merged.add(toListItemFound(e)));
        merged.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        List<AdminPostDto.ListItem> pageContent = merged.size() > want ? merged.subList(0, want) : merged;

        long totalElements = lostPage.getTotalElements() + foundPage.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return AdminPostDto.PageResponse.<AdminPostDto.ListItem>builder()
                .content(pageContent)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .build();
    }

    /** 상세 보기 */
    public AdminPostDto.Detail getDetail(String type, Long postId) {
        if ("LOST".equalsIgnoreCase(type)) {
            PostLost postLost = lostRepo.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));
            return toDetailLost(postLost);
        }
        if ("FOUND".equalsIgnoreCase(type)) {
            PostFound postFound = foundRepo.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));
            return toDetailFound(postFound);
        }
        throw new GeneralException(ErrorCode.CANNOT_ACCESS_DETAIL);
    }

    /** 관리자 삭제(소프트 삭제) */
    @Transactional
    public void delete(String type, Long postId) {
        if ("LOST".equalsIgnoreCase(type)) {
            PostLost postLost = lostRepo.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));
            if (!postLost.isDeletedByAdmin()) postLost.markDeletedByAdmin();
            return;
        }
        if ("FOUND".equalsIgnoreCase(type)) {
            PostFound postFound = foundRepo.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorCode.POST_NOT_FOUND));
            if (!postFound.isDeletedByAdmin()) postFound.markDeletedByAdmin();
            return;
        }
        throw new GeneralException(ErrorCode.CANNOT_DELETE_POST);
    }


    /* ===================== Mappers ===================== */

    private AdminPostDto.PageResponse<AdminPostDto.ListItem> toPage(Page<AdminPostDto.ListItem> page) {
        return AdminPostDto.PageResponse.<AdminPostDto.ListItem>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .page(page.getNumber())
                .size(page.getSize())
                .build();
    }

    private AdminPostDto.ListItem toListItemLost(PostLost postLost) {
        return AdminPostDto.ListItem.builder()
                .postId(postLost.getId())
                .type("LOST")
                .status(postLost.getStatus().getDescription()) // DogStatus
                .thumbnailUrl(s3Service.generatePresignedUrl(resolveThumb(postLost.getAiImage(), postLost.getRealImage())))
                .title(postLost.getTitle())
                .authorName(postLost.getMember().getMemberName())
                .createdAt(postLost.getCreatedAt())
                .region(postLost.getLostRegion())
                .build();
    }

    private AdminPostDto.ListItem toListItemFound(PostFound postFound) {
        return AdminPostDto.ListItem.builder()
                .postId(postFound.getId())
                .type("FOUND")
                .status(postFound.getStatus().getDescription()) // DogStatus
                .thumbnailUrl(s3Service.generatePresignedUrl(resolveThumb(postFound.getAiImage(), postFound.getRealImage())))
                .title(postFound.getTitle())
                .authorName(postFound.getMember().getMemberName())
                .createdAt(postFound.getCreatedAt())
                .region(postFound.getFoundRegion())
                .build();
    }

    private AdminPostDto.Detail toDetailLost(PostLost postLost) {
        double[] latlon = toLatLon(postLost.getLostSpot()); // Point → [lat, lon]
        return AdminPostDto.Detail.builder()
                .postId(postLost.getId())
                .type("LOST")
                .status(postLost.getStatus().getDescription())
                .title(postLost.getTitle())
                .authorName(postLost.getMember().getMemberName())
                .createdAt(postLost.getCreatedAt())
                .region(postLost.getLostRegion())
                .aiImage(s3Service.generatePresignedUrl(postLost.getAiImage()))
                .realImages(s3Service.generatePresignedUrls(postLost.getRealImage()))
                .dogName(s(postLost.getDogName()))
                .breed(postLost.getDogType() != null ? postLost.getDogType().getType() : null)
                .color(s(postLost.getDogColor()))
                .gender(postLost.getDogGender())
                .description(s(postLost.getContent()))
                // 시간 필드: LocalDate(lostDate) + LocalDateTime(lostTime)
                .eventDateTime(combine(postLost.getLostDate(), postLost.getLostTime()))
                .latitude(latlon[0])   // Y = lat
                .longitude(latlon[1])  // X = lon
                .build();
    }

    private AdminPostDto.Detail toDetailFound(PostFound postFound) {
        double[] latlon = toLatLon(postFound.getFoundSpot());
        return AdminPostDto.Detail.builder()
                .postId(postFound.getId())
                .type("FOUND")
                .status(postFound.getStatus().getDescription())
                .title(postFound.getTitle())
                .authorName(postFound.getMember().getMemberName())
                .createdAt(postFound.getCreatedAt())
                .region(postFound.getFoundRegion())
                .aiImage(s3Service.generatePresignedUrl(postFound.getAiImage()))
                .realImages(s3Service.generatePresignedUrls(postFound.getRealImage()))
                .dogName(null) // 목격글엔 보통 이름 없음(필요하면 p.getDogName()으로 교체)
                .breed(postFound.getDogType() != null ? postFound.getDogType().getType() : null)
                .color(s(postFound.getDogColor()))
                .gender(postFound.getDogGender())
                .description(s(postFound.getContent()))
                // 시간 필드: LocalDate(foundDate) + LocalDateTime(foundTime)
                .eventDateTime(combine(postFound.getFoundDate(), postFound.getFoundTime()))
                .latitude(latlon[0])
                .longitude(latlon[1])
                .build();
    }

    private String resolveThumb(String aiImage, List<String> realImage) {
        if (aiImage != null && !aiImage.isBlank()) return aiImage.trim();
        if (realImage != null && !realImage.isEmpty()) {
            String first = realImage.get(0);
            if (first != null && !first.isBlank()) return first.trim();
        }
        return null;
    }

    private String s(String v) { return (v == null || v.isBlank()) ? null : v; }

    /** LocalDate + LocalDateTime(시간) → LocalDateTime */
    private LocalDateTime combine(LocalDate date, LocalDateTime time) {
        if (date == null && time == null) return null;
        if (date != null && time == null) return date.atStartOfDay();
        if (date == null) return time;
        return LocalDateTime.of(date, time.toLocalTime());
    }

    /** PostGIS Point(4326) → [lat, lon] (Y=lat, X=lon) */
    private double[] toLatLon(Point point) {
        if (point == null) return new double[]{0d, 0d};
        return new double[]{point.getY(), point.getX()};
    }
}
