package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.domain.auth.SocialType
import nmnb.domain.user.User

interface AuthService {
    fun signInWithSocial(accessCode: String, type: SocialType, deviceId: String): AuthUserResponse
    fun refreshToken(refreshToken: String, deviceId: String): AuthTokenResponse
    fun logout(user: User, deviceId: String, accessToken: String, refreshToken: String)
}
