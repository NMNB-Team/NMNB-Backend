package nmnb.common.response.status

import nmnb.common.response.base.BaseErrorCode
import nmnb.common.response.dto.ErrorReasonDTO
import org.springframework.http.HttpStatus

enum class ErrorStatus(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
) : BaseErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // AUTH
    OAUTH_RESPONSE_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500", "OAuth 서버 응답을 처리하는 중 오류가 발생했습니다."),
    UNSUPPORTED_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "AUTH400", "지원하지 않는 소셜 로그인 타입입니다."),
    AUTH_INVALID_AUTH_PRINCIPAL(HttpStatus.UNAUTHORIZED, "AUTH401", "잘못된 인증 정보입니다."),
    AUTH_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH402", "잘못된 토큰 정보입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "AUTH403", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "AUTH404", "지원하지 않는 토큰입니다."),
    AUTH_ACCESS_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH405", "Access Token이 요청에 포함되어 있지 않습니다."),
    AUTH_EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH406", "빈 토큰이 전달되었습니다."),
    AUTH_CLAIM_EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH407", "토큰에 이메일 정보가 없습니다."),
    DEVICE_ID_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH408", "인증되지 않는 기기입니다."),
    DEVICE_ID_MISSING(HttpStatus.UNAUTHORIZED, "AUTH409", "디바이스 기기 정보가 요청에 포함되어 있지 않습니다."),
    AUTH_REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH410", "Refresh Token이 요청에 포함되어 있지 않습니다."),
    TOKEN_LOGGED_OUT(HttpStatus.FORBIDDEN, "AUTH411", "이미 로그아웃되어 무효화된 토큰입니다."),

    // POST
    POST_NOTFOUND(HttpStatus.NOT_FOUND, "POST400", "게시물을 찾을 수 없습니다."),
    POST_THUMBNAIL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "POST500", "영상 썸네일 생성 중 오류가 발생했습니다."),
    S3_DOWNLOAD_POST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "POST501", "영상을 다운로드하는 데 실패했습니다"),

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER400", "사용자를 찾을 수 없습니다."),
    S3_UPLOAD_PROFILE_IMAGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER500", "프로필 이미지 업로드 중 오류가 발생했습니다."),

    // PET
    PET_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "PET400", "반려동물 이름은 필수입니다."),
    PET_REGISTRATION_REQUIRED(HttpStatus.BAD_REQUEST, "PET401", "반려동물 등록이 필요합니다."),
    INVALID_PET_STATUS(HttpStatus.BAD_REQUEST, "PET402", "반려동물 상태 값이 유효하지 않습니다."),
    ;

    override fun getReasonHttpStatus(): ErrorReasonDTO {
        return ErrorReasonDTO(httpStatus, code, message)
    }
}
