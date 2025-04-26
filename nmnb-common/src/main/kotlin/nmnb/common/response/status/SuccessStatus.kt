package nmnb.common.response.status

import nmnb.common.response.base.BaseCode
import nmnb.common.response.dto.ReasonDTO
import org.springframework.http.HttpStatus

enum class SuccessStatus(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
) : BaseCode {
    OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성됨"),
    NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON202", "요청 성공 및 반환할 콘텐츠가 없음"),
    ;

    override fun getReasonHttpStatus(): ReasonDTO {
        return ReasonDTO(httpStatus, code, message)
    }
}
