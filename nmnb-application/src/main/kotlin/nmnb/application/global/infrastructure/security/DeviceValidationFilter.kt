package nmnb.application.global.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nmnb.application.global.common.utils.ResponseUtils
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
    private val responseUtils: ResponseUtils,
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
                    throw AuthException(ErrorStatus.DEVICE_ID_MISMATCH)
                }
            } catch (e: AuthException) {
                responseUtils.sendErrorResponse(response, ErrorStatus.DEVICE_ID_MISMATCH.getReasonHttpStatus())
                return
            } catch (e: Exception) {
                responseUtils.sendErrorResponse(response, ErrorStatus.UNAUTHORIZED.getReasonHttpStatus())
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
