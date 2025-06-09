package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.domain.auth.SocialType

interface AuthService {
    fun signInWithSocial(accessCode: String, type: SocialType, deviceId: String): AuthUserResponse
    fun refreshToken(refreshToken: String, deviceId: String): AuthTokenResponse
}
