package nmnb.application.global.infrastructure.security

import nmnb.common.auth.RefreshToken
import nmnb.common.auth.repository.RefreshTokenRepository
import nmnb.common.properties.JwtProperties
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.security.jwt.BaseJwtProvider
import nmnb.common.utils.JwtConstants.EMAIL_CLAIM_KEY
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class JwtProvider(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) : BaseJwtProvider(jwtProperties) {
    override fun createRefreshToken(now: Instant, email: String, deviceId: String): String {
        val refreshToken = generateJwt(now, email, jwtProperties.refreshExpirationTime)
        saveRefreshToken(email, deviceId, refreshToken, now)
        return refreshToken
    }

    private fun saveRefreshToken(
        email: String,
        deviceId: String,
        refreshToken: String,
        now: Instant?,
    ) {
        val redisKey = "$email:$deviceId"
        val timeStamp = now?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            ?: LocalDateTime.now()

        val token = RefreshToken(
            id = redisKey,
            email = email,
            refreshToken = refreshToken,
            timeStamp = timeStamp,
            deviceId = deviceId,
        )
        refreshTokenRepository.save(token)
    }

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
