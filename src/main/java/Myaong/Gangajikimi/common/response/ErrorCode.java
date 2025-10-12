package Myaong.Gangajikimi.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Schema(description = "에러 응답 코드")
public enum ErrorCode {

    // COMMON
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON400", "유효하지 않는 값입니다."),

    // AUTH
    INVALIDATE_TOKEN(HttpStatus.BAD_REQUEST, "AUTH400", "토큰 정보가 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401", "AccessToken 유효 기간이 만료되었습니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH402", "토큰을 찾을 수 없습니다."),
    INVALIDATE_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH403", "비밀번호가 일치하지 않아요."),

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER400", "존재하지 않는 회원 ID입니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "MEMBER401", "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "MEMBER402", "이미 사용 중인 닉네임입니다."),
    MEMBER_LOCKED(HttpStatus.BAD_REQUEST, "MEMBER404", "정지된 계정입니다."),

    // MESSAGE
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE400", "해당 메세지는 존재하지 않습니다."),
    CANNOT_DELETE_MASSAGE(HttpStatus.UNAUTHORIZED,"MESSAGE401","본인의 메세지만 삭제할 수 있습니다."),

    //CHATROOM
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM400", "해당 채팅방은 존재하지 않습니다."),
    NOT_SAME_PEOPLE(HttpStatus.BAD_REQUEST, "CHATROOM4001", "본인과의 채팅은 생성할 수 없습니다."),
    POST_NO_AUTH(HttpStatus.BAD_REQUEST, "CHATROOM4002", "해당 채팅방에 권한이 없습니다."),

    //SIGHT_CARD
    ALREADY_EXIST_CARD(HttpStatus.BAD_REQUEST, "SIGHTCARD400", "이미 발견카드가 존재합니다."),
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "SIGHTCARD4001", "발견카드가 존재하지 않습니다."),

    //NOTIFICATION
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION400", "존재하지 않는 알림입니다."),
    CANNOT_READ_NOTIFICATION(HttpStatus.BAD_REQUEST, "NOTIFICATION4001", "본인의 알림만 읽음 처리할 수 있습니다."),

    //POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST400", "존재하지 않는 게시글입니다."),
    UNAUTHORIZED_UPDATING(HttpStatus.UNAUTHORIZED, "POST401", "게시글 수정 권한이 없습니다."),
    UNAUTHORIZED_DELETING(HttpStatus.UNAUTHORIZED, "POST402", "게시글 삭제 권한이 없습니다."),
    CANNOT_REPORT_OWN_POST(HttpStatus.BAD_REQUEST, "POST403", "본인의 게시글은 신고할 수 없습니다."),
    ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "POST404", "이미 신고한 게시글입니다."),
    //FILTER
    LOCATION_REQUIRED(HttpStatus.BAD_REQUEST, "POST405", "사용자의 위치 정보가 필요합니다."),
    INVALID_TIME_FILTER(HttpStatus.BAD_REQUEST, "POST406", "적절하지 않은 시간입니다."),

    //FASTAPI
    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI500", "AI 서버로부터 응답을 가져오는 데 실패했습니다."),

    //S3
    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, "S3401", "파일이 비어 있습니다."),
    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "S3402", "파일에 확장자가 없습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "S3403", "지원하지 않는 파일 확장자입니다. (jpg, jpeg, png, gif 만 허용)"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "S3404", "파일 크기가 제한을 초과했습니다. (최대 5MB)"),
    INVALID_S3_URL(HttpStatus.BAD_REQUEST, "S3405", "유효하지 않은 S3 URL입니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "S3501", "이미지 업로드 중 IO 예외가 발생했습니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "S3502", "이미지 삭제 중 예외가 발생했습니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S3503", "S3에 파일 업로드 중 예외가 발생했습니다."),

    //DOGTYPE
    DOG_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "DOGTYPE404", "해당 견종을 찾을 수 없습니다."),

    //KAKAO
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION404", "행정구역 정보를 불러올 수 없습니다."),
    KAKAO_SERVICE_AREA_ERROR(HttpStatus.BAD_REQUEST, "KAKAO400", "서비스 영역 밖의 주소입니다. 대한민국 내 주소를 입력해주세요."),

    //ADMIN
    CANNOT_DELETE_POST(HttpStatus.NOT_FOUND, "DELETE400", "삭제 처리가 되지 않았습니다."),
    CANNOT_ACCESS_DETAIL(HttpStatus.NOT_FOUND, "DETAIL400", "게시글 상세 정보를 조회할 수 없습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;

}