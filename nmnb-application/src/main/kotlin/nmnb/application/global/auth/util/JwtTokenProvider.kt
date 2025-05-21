package nmnb.application.global.auth.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import nmnb.application.global.auth.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class JwtTokenProvider(
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
            .claim("email", email)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(email: String): String {
        val refrehToken = Jwts.builder()
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(refreshExpirationTime, ChronoUnit.SECONDS)))
            .claim("email", email)
            .signWith(key, SignatureAlgorithm.HS256).compact()

        refreshTokenRepository.findByIdOrNull(email)?.update(refrehToken) ?: refreshTokenRepository.save(
            RefreshToken(email, refrehToken),
        )
        return refrehToken
    }

    fun getEmailWithValidation(token: String): String {
        val claims = parseClaims(token)
        return claims["email"] as? String
            ?: throw AuthException(ErrorStatus.AUTH_CLAIM_EMAIL_NOT_FOUND)
    }

    private fun parseClaims(token: String): Claims {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: ExpiredJwtException) {
            throw AuthException(ErrorStatus.AUTH_EXPIRED_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw AuthException(ErrorStatus.UNSUPPORTED_TOKEN)
        } catch (e: MalformedJwtException) {
            throw AuthException(ErrorStatus.AUTH_INVALID_TOKEN)
        } catch (e: SignatureException) {
            throw AuthException(ErrorStatus.AUTH_INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw AuthException(ErrorStatus.AUTH_EMPTY_TOKEN)
        }
    }
}
