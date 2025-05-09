package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthUserResponse

interface AuthService {
    fun signInWithSocial(accessCode: String): AuthUserResponse
}
