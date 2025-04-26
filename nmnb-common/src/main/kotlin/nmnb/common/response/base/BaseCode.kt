package nmnb.common.response.base

import nmnb.common.response.dto.ReasonDTO

interface BaseCode {
    fun getReasonHttpStatus(): ReasonDTO
}
