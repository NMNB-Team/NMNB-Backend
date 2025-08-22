package nmnb.application.global.infrastructure.security

import nmnb.common.auth.repository.RefreshTokenRepository
import nmnb.common.properties.JwtProperties
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.security.jwt.BaseJwtProvider
import nmnb.common.utils.JwtConstants.EMAIL_CLAIM_KEY
import org.springframework.stereotype.Component

@Component
class JwtProvider(
    refreshTokenRepository: RefreshTokenRepository,
    jwtProperties: JwtProperties,
) : BaseJwtProvider(refreshTokenRepository, jwtProperties) {

    fun getEmailWithValidation(token: String): String {
        val claims = parseClaims(token)
        return claims[EMAIL_CLAIM_KEY] as? String
            ?: throw AuthException(ErrorStatus.AUTH_CLAIM_EMAIL_NOT_FOUND)
    }

    fun getRemainingTtl(token: String): Long {
        val claims = parseClaims(token)
        val expiration = claims.expiration.time
        val now = System.currentTimeMillis()

        return expiration - now
    }
}
