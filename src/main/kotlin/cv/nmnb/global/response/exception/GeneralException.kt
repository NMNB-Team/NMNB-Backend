package cv.nmnb.global.response.exception

import cv.nmnb.global.response.base.BaseErrorCode
import cv.nmnb.global.response.dto.ErrorReasonDTO

open class GeneralException(
    private val code: BaseErrorCode,
) : RuntimeException() {

    fun getErrorReasonHttpStatus(): ErrorReasonDTO {
        return this.code.getReasonHttpStatus()
    }
}
