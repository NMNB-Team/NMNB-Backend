package nmnb.common.response.dto

import org.springframework.http.HttpStatus

data class ErrorReasonDTO(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
    val isSuccess: Boolean = false,
)
