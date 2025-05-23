package nmnb.application.global.auth.service.dto.response

import nmnb.common.domain.SignUpStatus

data class AuthUserResponse(
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val signUpStatus: SignUpStatus,
)
