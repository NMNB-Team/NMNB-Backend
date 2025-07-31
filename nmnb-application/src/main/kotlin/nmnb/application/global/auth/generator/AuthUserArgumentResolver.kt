package nmnb.application.global.auth.generator

import nmnb.common.handler.annotation.AuthUser
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.core.MethodParameter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthUserArgumentResolver(
    private val userRepository: UserRepository,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return (
            parameter.parameterType == User::class.java &&
                parameter.hasParameterAnnotation(AuthUser::class.java)
            )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        val userEmail = getUserEmail(authentication)
        return userRepository.findByEmail(userEmail) ?: throw AuthException(ErrorStatus.USER_NOT_FOUND)
    }

    companion object {
        private fun getUserEmail(authentication: Authentication): String {
            if (authentication.name == "anonymousUser") {
                throw AuthException(ErrorStatus.UNAUTHORIZED)
            }

            val principal = authentication.principal
            if (principal is String) {
                throw AuthException(ErrorStatus.AUTH_INVALID_AUTH_PRINCIPAL)
            }

            val authenticationToken = authentication as UsernamePasswordAuthenticationToken
            return authenticationToken.name
        }
    }
}
