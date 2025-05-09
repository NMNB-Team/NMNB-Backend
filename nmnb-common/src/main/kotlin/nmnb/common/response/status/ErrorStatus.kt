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
    KAKAO_RESPONSE_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500", "카카오 서버 응답을 처리하는 중 오류가 발생했습니다."),

    // POST
    POST_NOTFOUND(HttpStatus.NOT_FOUND, "POST400", "게시물을 찾을 수 없습니다."),
    ;

    override fun getReasonHttpStatus(): ErrorReasonDTO {
        return ErrorReasonDTO(httpStatus, code, message)
    }
}
