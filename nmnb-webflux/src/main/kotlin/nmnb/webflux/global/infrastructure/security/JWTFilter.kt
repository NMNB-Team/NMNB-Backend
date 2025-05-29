package nmnb.webflux.global.infrastructure.security

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
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: R2dbcUserRepository,
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authorizationHeader = exchange.request.headers.getFirst(AUTHORIZATION_HEADER)

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return chain.filter(exchange)
        }

        val parsedToken = authorizationHeader.substring(7)

        return if (jwtTokenProvider.isValidToken(parsedToken)) {
            val username = jwtTokenProvider.getEmail(parsedToken)

            return userRepository.findByEmail(username)
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
        } else {
            Mono.error(GeneralException(ErrorStatus.AUTH_INVALID_TOKEN))
        }
    }
    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
