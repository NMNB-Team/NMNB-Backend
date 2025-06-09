package nmnb.application.global.auth.generator

import nmnb.application.global.auth.generator.annotation.ExtractToken
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class ExtractTokenArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == String::class.java && parameter.hasParameterAnnotation(
            ExtractToken::class.java,
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val authorizationHeader = webRequest.getHeader(AUTHORIZATION_HEADER)
            ?: throw AuthException(ErrorStatus.AUTH_TOKEN_MISSING)

        return authorizationHeader.substring(7)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
