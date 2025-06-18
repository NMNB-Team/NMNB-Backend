package nmnb.application.global.auth.generator

import nmnb.application.global.auth.generator.annotation.ExtractRefreshToken
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class ExtractRefreshTokenArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == String::class.java && parameter.hasParameterAnnotation(
            ExtractRefreshToken::class.java,
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        return webRequest.getHeader(REFRESH_TOKEN_HEADER)
            ?: throw AuthException(ErrorStatus.AUTH_REFRESH_TOKEN_MISSING)
    }

    companion object {
        const val REFRESH_TOKEN_HEADER = "X-Refresh-Token"
    }
}
