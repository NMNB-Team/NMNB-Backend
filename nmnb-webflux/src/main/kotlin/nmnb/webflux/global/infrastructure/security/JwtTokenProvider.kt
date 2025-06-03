package nmnb.webflux.global.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
) {
    fun isValidToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            val now = java.util.Date()
            val expiredDate = claims.body.expiration
            expiredDate.after(now)
        } catch (e: ExpiredJwtException) {
            throw GeneralException(ErrorStatus.AUTH_EXPIRED_TOKEN)
        } catch (e: SecurityException) {
            throw GeneralException(ErrorStatus.AUTH_INVALID_TOKEN)
        } catch (e: MalformedJwtException) {
            throw GeneralException(ErrorStatus.AUTH_INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw GeneralException(ErrorStatus.AUTH_INVALID_TOKEN)
        } catch (e: UnsupportedJwtException) {
            throw GeneralException(ErrorStatus.UNSUPPORTED_TOKEN)
        }
    }

    fun getEmail(token: String): String {
        return getClaims(token).body.get("email", String::class.java)
    }

    private fun getClaims(token: String): Jws<Claims> {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
    }

    private fun getSigningKey(): SecretKey? {
        val keyBytes = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
