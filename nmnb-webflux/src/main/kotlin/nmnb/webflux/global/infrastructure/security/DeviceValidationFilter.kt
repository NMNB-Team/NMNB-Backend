package nmnb.webflux.global.infrastructure.security

import nmnb.common.response.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.HeaderConstants.ACCESS_TOKEN_HEADER
import nmnb.common.utils.HeaderConstants.DEVICE_ID_HEADER
import nmnb.common.utils.JwtConstants.DEVICE_ID_CLAIM_KEY
import nmnb.webflux.global.utils.ResponseUtils
import nmnb.webflux.global.utils.SecurityConstants
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class DeviceValidationFilter(
    private val jwtProvider: JwtProvider,
    private val responseUtils: ResponseUtils,
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        if (SecurityConstants.SWAGGER_PATHS.any { swaggerPath ->
                path == swaggerPath || path.startsWith("$swaggerPath/")
            }
        ) {
            return chain.filter(exchange)
        }

        val accessToken = exchange.request.headers.getFirst(ACCESS_TOKEN_HEADER)

        if (accessToken != null) {
            return try {
                val deviceIdInToken = jwtProvider.getClaimFromToken(accessToken, DEVICE_ID_CLAIM_KEY) as? String
                val deviceIdInRequest = exchange.request.headers.getFirst(DEVICE_ID_HEADER)

                val validationMono = when {
                    deviceIdInRequest == null ->
                        Mono.error(AuthException(ErrorStatus.DEVICE_ID_MISSING))
                    deviceIdInToken == null || deviceIdInToken != deviceIdInRequest ->
                        Mono.error(AuthException(ErrorStatus.DEVICE_ID_MISMATCH))
                    else ->
                        chain.filter(exchange)
                }

                validationMono
            } catch (e: Exception) {
                Mono.error(AuthException(ErrorStatus.UNAUTHORIZED))
            }.onErrorResume { throwable ->
                when (throwable) {
                    is GeneralException -> responseUtils.sendErrorResponse(
                        exchange,
                        throwable.getErrorReasonHttpStatus(),
                    )
                    else -> responseUtils.sendErrorResponse(
                        exchange,
                        ErrorStatus.UNAUTHORIZED.getReasonHttpStatus(),
                    )
                }
            }
        }

        return Mono.error(GeneralException(ErrorStatus.AUTH_INVALID_TOKEN))
    }
}
