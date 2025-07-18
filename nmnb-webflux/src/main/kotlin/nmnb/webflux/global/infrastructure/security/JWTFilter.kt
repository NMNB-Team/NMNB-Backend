package nmnb.webflux.global.infrastructure.security

import nmnb.common.response.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.HeaderConstants.ACCESS_TOKEN_HEADER
import nmnb.r2dbc.user.R2dbcUserRepository
import nmnb.webflux.global.auth.domain.CustomUserDetails
import nmnb.webflux.global.utils.ResponseUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JWTFilter(
    private val jwtProvider: JwtProvider,
    private val userRepository: R2dbcUserRepository,
    private val blacklistService: BlacklistService,
    private val responseUtils: ResponseUtils,
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val accessToken =
            exchange.request.headers.getFirst(ACCESS_TOKEN_HEADER) ?: return chain.filter(exchange)

        return try {
            if (!jwtProvider.isValidToken(accessToken)) return Mono.error(GeneralException(ErrorStatus.AUTH_INVALID_TOKEN))

            blacklistService.isBlacklisted(accessToken).flatMap { isBlacklisted ->
                if (isBlacklisted) {
                    return@flatMap Mono.error(AuthException(ErrorStatus.TOKEN_LOGGED_OUT))
                }
                val username = jwtProvider.getEmail(accessToken)

                return@flatMap userRepository.findByEmail(username)
                    .switchIfEmpty(Mono.error(GeneralException(ErrorStatus.USER_NOT_FOUND)))
                    .flatMap { user ->
                        val userDetails = CustomUserDetails(user)

                        val authenticationToken = UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities,
                        )

                        chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken))
                    }
            }

        } catch (e: Exception) {
            Mono.error(e)
        }.onErrorResume { throwable ->
            when (throwable) {
                is GeneralException -> responseUtils.sendErrorResponse(
                    exchange,
                    throwable.getErrorReasonHttpStatus()
                )
                else -> responseUtils.sendErrorResponse(
                    exchange,
                    ErrorStatus.UNAUTHORIZED.getReasonHttpStatus()
                )
            }
        }
    }
}
