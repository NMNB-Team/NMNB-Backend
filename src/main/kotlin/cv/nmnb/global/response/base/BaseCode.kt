package cv.nmnb.global.response.base

import cv.nmnb.global.response.dto.ReasonDTO

interface BaseCode {
    fun getReasonHttpStatus(): ReasonDTO
}
