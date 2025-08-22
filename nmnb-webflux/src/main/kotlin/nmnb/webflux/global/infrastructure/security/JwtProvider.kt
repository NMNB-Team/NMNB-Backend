package nmnb.webflux.global.infrastructure.security

import nmnb.common.auth.repository.RefreshTokenRepository
import nmnb.common.properties.JwtProperties
import nmnb.common.security.jwt.BaseJwtProvider
import nmnb.common.utils.JwtConstants.EMAIL_CLAIM_KEY
import org.springframework.stereotype.Component

@Component
class JwtProvider(
    refreshTokenRepository: RefreshTokenRepository,
    jwtProperties: JwtProperties,
) : BaseJwtProvider(refreshTokenRepository, jwtProperties) {

    fun isValidToken(token: String): Boolean {
        val claims = parseClaims(token)
        val now = java.util.Date()
        return claims.expiration.after(now)
    }

    fun getEmail(token: String): String {
        return parseClaims(token).get(EMAIL_CLAIM_KEY, String::class.java)
    }
}
