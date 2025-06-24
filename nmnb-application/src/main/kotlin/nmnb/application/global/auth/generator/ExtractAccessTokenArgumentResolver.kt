package nmnb.application.global.auth.generator

import nmnb.application.global.auth.generator.annotation.ExtractAccessToken
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.HeaderConstants.ACCESS_TOKEN_HEADER
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class ExtractAccessTokenArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == String::class.java && parameter.hasParameterAnnotation(
            ExtractAccessToken::class.java,
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        return webRequest.getHeader(ACCESS_TOKEN_HEADER)
            ?: throw AuthException(ErrorStatus.AUTH_ACCESS_TOKEN_MISSING)
    }
}
