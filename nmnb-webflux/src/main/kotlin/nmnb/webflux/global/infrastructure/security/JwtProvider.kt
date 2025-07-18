package nmnb.webflux.global.infrastructure.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.JwtConstants.EMAIL_CLAIM_KEY
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SignatureException

@Component
class JwtProvider(
    @Value("\${jwt.secret}") private val secret: String,
) {
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)) }

    fun isValidToken(token: String): Boolean {
        val claims = parseClaims(token)
        val now = java.util.Date()
        return claims.expiration.after(now)
    }

    fun getEmail(token: String): String {
        return parseClaims(token).get(EMAIL_CLAIM_KEY, String::class.java)
    }

    fun getClaimFromToken(token: String, claimKey: String): Any? {
        val claims = parseClaims(token)
        return claims[claimKey]
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
