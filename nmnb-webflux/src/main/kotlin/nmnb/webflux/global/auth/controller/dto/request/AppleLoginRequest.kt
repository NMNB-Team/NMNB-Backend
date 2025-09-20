package nmnb.webflux.global.auth.controller.dto.request

import nmnb.webflux.global.auth.service.dto.request.AppleLoginServiceRequest

data class AppleLoginRequest(
    val identityToken: String,
) {
    fun toAppleLoginServiceRequest(): AppleLoginServiceRequest {
        return AppleLoginServiceRequest(identityToken)
    }
}
