package nmnb.application.global.auth.service.dto.response

import nmnb.domain.user.SignUpStatus

data class AuthUserResponse(
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val signUpStatus: SignUpStatus,
)
