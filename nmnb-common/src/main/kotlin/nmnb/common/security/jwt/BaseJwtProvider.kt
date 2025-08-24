package nmnb.common.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import nmnb.common.properties.JwtProperties
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.JwtConstants.DEVICE_ID_CLAIM_KEY
import nmnb.common.utils.JwtConstants.EMAIL_CLAIM_KEY
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.sql.Date
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
abstract class BaseJwtProvider(
    private val jwtProperties: JwtProperties,
) {

    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret)) }

    fun createAccessToken(now: Instant, email: String, deviceId: String) =
        generateJwt(now, email, jwtProperties.accessExpirationTime, deviceId)

    fun createRefreshToken(now: Instant, email: String, deviceId: String): String {
        return generateJwt(now, email, jwtProperties.refreshExpirationTime)
    }

    protected fun generateJwt(now: Instant, email: String, expirationTime: Long, deviceId: String? = null): String {
        val builder = Jwts.builder()
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(Instant.now().plus(expirationTime, ChronoUnit.SECONDS)))
            .claim(EMAIL_CLAIM_KEY, email)

        deviceId?.let {
            builder.claim(DEVICE_ID_CLAIM_KEY, it)
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact()
    }

    protected fun parseClaims(token: String): Claims {
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

    fun getClaimFromToken(token: String, claimKey: String): Any? {
        val claims = parseClaims(token)
        return claims[claimKey]
    }
}
