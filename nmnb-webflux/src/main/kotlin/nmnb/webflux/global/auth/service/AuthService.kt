package nmnb.webflux.global.auth.service

import nmnb.webflux.global.auth.service.dto.request.AppleLoginServiceRequest
import nmnb.webflux.global.auth.service.dto.response.AuthUserResponse
import reactor.core.publisher.Mono

interface AuthService {
    fun appleLogin(request: AppleLoginServiceRequest, deviceId: String): Mono<AuthUserResponse>
}
