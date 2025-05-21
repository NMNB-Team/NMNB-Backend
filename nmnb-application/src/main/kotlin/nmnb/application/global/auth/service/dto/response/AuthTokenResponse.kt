package nmnb.application.global.auth.service.dto.response

data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
