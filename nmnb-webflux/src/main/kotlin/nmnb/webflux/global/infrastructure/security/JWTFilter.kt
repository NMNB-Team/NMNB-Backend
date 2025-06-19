package nmnb.webflux.global.infrastructure.security

import nmnb.common.response.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import nmnb.r2dbc.user.R2dbcUserRepository
import nmnb.webflux.global.auth.domain.CustomUserDetails
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
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val accessToken =
            exchange.request.headers.getFirst(ACCESS_TOKEN_HEADER) ?: return chain.filter(exchange)
        if (!jwtProvider.isValidToken(accessToken)) return Mono.error(GeneralException(ErrorStatus.AUTH_INVALID_TOKEN))

        return blacklistService.isBlacklisted(accessToken).flatMap { isBlacklisted ->
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
    }

    companion object {
        const val ACCESS_TOKEN_HEADER = "X-Access-Token"
    }
}
