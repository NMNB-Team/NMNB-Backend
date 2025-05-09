package nmnb.application.global.auth.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-expiration-time}") private val accessExpirationTime: Long,
    @Value("\${jwt.refresh-expiration-time}") private val refreshExpirationTime: Long,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)) }

    fun createAccessToken(email: String): String {
        return Jwts.builder()
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(accessExpirationTime, ChronoUnit.SECONDS)))
            .claim("userId", email)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(email: String): String {
        val refrehToken = Jwts.builder()
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(refreshExpirationTime, ChronoUnit.SECONDS)))
            .signWith(key, SignatureAlgorithm.HS256).compact()

        refreshTokenRepository.findByIdOrNull(email)?.updateRefreshToken(refrehToken) ?: refreshTokenRepository.save(
            RefreshToken(email, refrehToken),
        )
        return refrehToken
    }
}
