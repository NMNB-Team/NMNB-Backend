package nmnb.webflux.global.infrastructure.security

import nmnb.common.auth.RefreshToken
import nmnb.common.auth.repository.RefreshTokenRepository
import nmnb.common.properties.JwtProperties
import nmnb.common.security.jwt.BaseJwtProvider
import nmnb.common.utils.JwtConstants.EMAIL_CLAIM_KEY
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class JwtProvider(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) : BaseJwtProvider(jwtProperties) {
    override fun createRefreshToken(now: Instant, email: String, deviceId: String): Mono<String> {
        val refreshToken = generateJwt(now, email, jwtProperties.refreshExpirationTime)
        return saveRefreshToken(email, deviceId, refreshToken, now)
            .thenReturn(refreshToken)
    }

    private fun saveRefreshToken(
        email: String,
        deviceId: String,
        refreshToken: String,
        now: Instant?,
    ): Mono<Void> {
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
        return Mono.fromCallable { refreshTokenRepository.save(token) }
            .subscribeOn(Schedulers.boundedElastic())
            .then()
    }

    fun isValidToken(token: String): Boolean {
        val claims = parseClaims(token)
        val now = java.util.Date()
        return claims.expiration.after(now)
    }

    fun getEmail(token: String): String {
        return parseClaims(token).get(EMAIL_CLAIM_KEY, String::class.java)
    }
}
