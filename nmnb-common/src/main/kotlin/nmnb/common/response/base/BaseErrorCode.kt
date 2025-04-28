package nmnb.common.response.base

import nmnb.common.response.dto.ErrorReasonDTO

interface BaseErrorCode {
    fun getReasonHttpStatus(): ErrorReasonDTO
}
