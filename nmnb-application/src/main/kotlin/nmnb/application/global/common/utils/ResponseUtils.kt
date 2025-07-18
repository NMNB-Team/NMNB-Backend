package nmnb.application.global.common.utils

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.dto.ErrorReasonDTO
import org.springframework.stereotype.Component

@Component
class ResponseUtils(
    private val objectMapper: ObjectMapper,
) {
    fun sendErrorResponse(
        response: HttpServletResponse,
        errorReasonDTO: ErrorReasonDTO,
    ) {
        response.status = errorReasonDTO.httpStatus.value()
        response.contentType = "application/json;charset=UTF-8"

        val body = BaseResponse.onFailure(
            errorReasonDTO.code,
            errorReasonDTO.message,
            null,
        )

        response.writer.write(objectMapper.writeValueAsString(body))
        response.writer.flush()
    }
}
