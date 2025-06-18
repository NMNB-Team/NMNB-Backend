package nmnb.application.global.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class DeviceValidationFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken = request.getHeader(ACCESS_TOKEN_HEADER)
        if (accessToken != null) {
            try {
                val deviceIdInToken = jwtProvider.getClaimFromToken(accessToken, DEVICE_ID_HEADER) as? String
                val deviceIdInRequest = request.getHeader(DEVICE_ID_HEADER)

                if (deviceIdInToken == null || deviceIdInRequest == null || deviceIdInToken != deviceIdInRequest) {
                    throw AuthException(ErrorStatus.DEVICE_ID_MISMATCH)
                }
            } catch (e: Exception) {
                throw AuthException(ErrorStatus.UNAUTHORIZED)
            }
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val ACCESS_TOKEN_HEADER = "X-Access-Token"
        const val DEVICE_ID_HEADER = "Device-Id"
    }
}
