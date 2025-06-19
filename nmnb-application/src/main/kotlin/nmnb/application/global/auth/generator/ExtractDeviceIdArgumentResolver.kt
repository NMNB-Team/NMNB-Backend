package nmnb.application.global.auth.generator

import nmnb.common.handler.annotation.ExtractDeviceId
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.HeaderConstants.DEVICE_ID_HEADER
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class ExtractDeviceIdArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == String::class.java && parameter.hasParameterAnnotation(
            ExtractDeviceId::class.java,
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        return webRequest.getHeader(DEVICE_ID_HEADER)
            ?: throw AuthException(ErrorStatus.DEVICE_ID_MISSING)
    }
}
