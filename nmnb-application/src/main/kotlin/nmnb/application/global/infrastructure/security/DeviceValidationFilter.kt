package nmnb.application.global.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.HeaderConstants.ACCESS_TOKEN_HEADER
import nmnb.common.utils.HeaderConstants.DEVICE_ID_HEADER
import nmnb.common.utils.JwtConstants.DEVICE_ID_CLAIM_KEY
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class DeviceValidationFilter(
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken = request.getHeader(ACCESS_TOKEN_HEADER)
        if (accessToken != null) {
            try {
                val deviceIdInToken = jwtProvider.getClaimFromToken(accessToken, DEVICE_ID_CLAIM_KEY) as? String
                val deviceIdInRequest = request.getHeader(DEVICE_ID_HEADER)

                if (deviceIdInToken == null || deviceIdInRequest == null || deviceIdInToken != deviceIdInRequest) {
                    sendErrorResponse(response, ErrorStatus.DEVICE_ID_MISMATCH)
                    return
                }
            } catch (e: Exception) {
                sendErrorResponse(response, ErrorStatus.UNAUTHORIZED)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun sendErrorResponse(response: HttpServletResponse, errorStatus: ErrorStatus) {
        response.status = errorStatus.httpStatus!!.value()
        response.contentType = "application/json;charset=UTF-8"

        val errorBody = BaseResponse.onFailure(
            errorStatus.code,
            errorStatus.message,
            null
        )

        response.writer.write(objectMapper.writeValueAsString(errorBody))
    }
}
