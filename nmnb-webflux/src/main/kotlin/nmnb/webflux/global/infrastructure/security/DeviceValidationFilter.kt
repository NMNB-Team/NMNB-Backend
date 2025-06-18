package nmnb.webflux.global.infrastructure.security

import nmnb.common.response.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class DeviceValidationFilter(
    private val jwtProvider: JwtProvider,
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val accessToken = exchange.request.headers.getFirst(ACCESS_TOKEN_HEADER)

        if (accessToken != null) {
            return try {
                val deviceIdInToken = jwtProvider.getClaimFromToken(accessToken, DEVICE_ID_HEADER) as? String
                val deviceIdInRequest = exchange.request.headers.getFirst(DEVICE_ID_HEADER)

                if (deviceIdInToken == null || deviceIdInRequest == null || deviceIdInToken != deviceIdInRequest) {
                    Mono.error(AuthException(ErrorStatus.DEVICE_ID_MISMATCH))
                } else {
                    chain.filter(exchange)
                }
            } catch (e: Exception) {
                Mono.error(AuthException(ErrorStatus.UNAUTHORIZED))
            }
        }

        return Mono.error(GeneralException(ErrorStatus.AUTH_INVALID_TOKEN))
    }

    companion object {
        const val ACCESS_TOKEN_HEADER = "X-Access-Token"
        const val DEVICE_ID_HEADER = "Device-Id"
    }
}
