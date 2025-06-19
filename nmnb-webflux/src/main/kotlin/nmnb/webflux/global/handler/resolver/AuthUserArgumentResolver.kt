package nmnb.webflux.global.handler.resolver

import nmnb.common.handler.annotation.AuthUser
import nmnb.common.response.exception.UserException
import nmnb.common.response.status.ErrorStatus
import nmnb.r2dbc.user.R2dbcUser
import nmnb.r2dbc.user.R2dbcUserRepository
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthUserArgumentResolver(
    private val userRepository: R2dbcUserRepository,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return (
            parameter.parameterType == R2dbcUser::class.java &&
                parameter.hasParameterAnnotation(AuthUser::class.java)
            )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val test = ReactiveSecurityContextHolder.getContext()
            .flatMap { securityContext ->
                val authentication = securityContext.authentication
                val email = authentication?.name
                    ?: return@flatMap Mono.error(UserException(ErrorStatus.UNAUTHORIZED))

                userRepository.findByEmail(email)
                    .switchIfEmpty(Mono.error(UserException(ErrorStatus.USER_NOT_FOUND)))
            }

        return test.cast(Any::class.java)
    }
}
