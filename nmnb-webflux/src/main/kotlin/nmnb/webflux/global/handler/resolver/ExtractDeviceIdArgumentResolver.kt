package nmnb.webflux.global.handler.resolver

import nmnb.common.handler.annotation.ExtractDeviceId
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ExtractDeviceIdArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == String::class.java && parameter.hasParameterAnnotation(
            ExtractDeviceId::class.java,
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val deviceId = exchange.request.headers.getFirst(DEVICE_ID_HEADER)
        return if (deviceId != null) {
            Mono.just(deviceId)
        } else {
            Mono.error(AuthException(ErrorStatus.DEVICE_ID_MISSING))
        }
    }

    companion object {
        const val DEVICE_ID_HEADER = "Device-Id"
    }
}
