package cv.nmnb.global.response.dto

import org.springframework.http.HttpStatus

data class ReasonDTO(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
    val isSuccess: Boolean = true,
)
