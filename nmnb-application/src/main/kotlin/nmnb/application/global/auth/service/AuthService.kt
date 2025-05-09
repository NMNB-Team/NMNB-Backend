package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthSignInResponse

interface AuthService {
    fun signInWithSocial(accessCode: String): AuthSignInResponse
}
