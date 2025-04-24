package cv.nmnb.global.response.base

import cv.nmnb.global.response.dto.ErrorReasonDTO

interface BaseErrorCode {
    fun getReasonHttpStatus(): ErrorReasonDTO
}
