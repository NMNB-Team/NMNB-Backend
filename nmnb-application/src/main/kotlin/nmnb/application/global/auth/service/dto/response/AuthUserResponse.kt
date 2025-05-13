package nmnb.application.global.auth.service.dto.response

data class AuthUserResponse(
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)
