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
    AUTH_INVALID_AUTH_PRINCIPAL(HttpStatus.UNAUTHORIZED, "AUTH501", "잘못된 인증 정보입니다."),
    AUTH_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH502", "잘못된 토큰 정보입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "AUTH503", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "AUTH504", "지원하지 않는 토큰입니다."),
    AUTH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "AUTH405", "토큰이 요청에 포함되어 있지 않습니다."),
    AUTH_EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH406", "빈 토큰이 전달되었습니다."),
    AUTH_CLAIM_EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH407", "토큰에 이메일 정보가 없습니다."),

    // POST
    POST_NOTFOUND(HttpStatus.NOT_FOUND, "POST400", "게시물을 찾을 수 없습니다."),
    POST_THUMBNAIL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "POST500", "영상 썸네일 생성 중 오류가 발생했습니다."),
    POST_S3_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "POST501", "S3에서 영상을 다운로드하는 데 실패했습니다"),

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER400", "사용자를 찾을 수 없습니다."),
    ;

    override fun getReasonHttpStatus(): ErrorReasonDTO {
        return ErrorReasonDTO(httpStatus, code, message)
    }
}
