package nmnb.common.response.exception

import nmnb.common.response.base.BaseErrorCode
import nmnb.common.response.dto.ErrorReasonDTO

open class GeneralException(
    private val code: BaseErrorCode,
) : RuntimeException() {

    fun getCode(): BaseErrorCode {
        return this.code
    }

    fun getErrorReasonHttpStatus(): ErrorReasonDTO {
        return this.code.getReasonHttpStatus()
    }
}
