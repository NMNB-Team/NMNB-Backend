package nmnb.webflux.global.utils

import com.fasterxml.jackson.databind.ObjectMapper
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.dto.ErrorReasonDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ResponseUtils(
    private val objectMapper: ObjectMapper,
) {
    fun sendErrorResponse(
        exchange: ServerWebExchange,
        errorReasonDTO: ErrorReasonDTO,
    ): Mono<Void> {
        val response = exchange.response

        response.statusCode = HttpStatus.valueOf(errorReasonDTO.httpStatus.value())
        response.headers.add("Content-Type", "application/json;charset=UTF-8")

        val body = BaseResponse.onFailure(
            errorReasonDTO.code,
            errorReasonDTO.message,
            null,
        )

        val bodyJson = objectMapper.writeValueAsString(body)
        val dataBuffer = response.bufferFactory().wrap(bodyJson.toByteArray())

        return response.writeWith(Mono.just(dataBuffer))
    }
}
