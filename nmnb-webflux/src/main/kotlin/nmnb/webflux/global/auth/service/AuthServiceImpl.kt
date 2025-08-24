package nmnb.webflux.global.auth.service

import nmnb.common.properties.S3Properties
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.r2dbc.user.R2dbcUser
import nmnb.r2dbc.user.R2dbcUserRepository
import nmnb.webflux.global.auth.service.dto.request.AppleLoginServiceRequest
import nmnb.webflux.global.auth.service.dto.response.AuthUserResponse
import nmnb.webflux.global.infrastructure.security.JwtProvider
import nmnb.webflux.global.properties.AppleProperties
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant

@Transactional(readOnly = true)
@Service
class AuthServiceImpl(
    private val jwtProvider: JwtProvider,
    private val appleProperties: AppleProperties,
    private val s3Properties: S3Properties,
    private val jwtDecoder: JwtDecoder,
    private val userRepository: R2dbcUserRepository,
    private val refreshTokenService: RefreshTokenService,
) : AuthService {

    @Transactional
    override fun appleLogin(request: AppleLoginServiceRequest, deviceId: String): Mono<AuthUserResponse> {
        // 토큰 검증
        val claims = validateAppleIdToken(request.identityToken)

        val email = claims["email"] as String

        // 회원가입 & 로그인
        return userRepository.findByEmail(email)
            .switchIfEmpty { createUser(email) }
            .flatMap { user ->
                issueNewToken(email, deviceId)
                    .map { (refreshToken, accessToken) ->
                        AuthUserResponse(
                            email = email,
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            signUpStatus = user.signUpStatus,
                        )
                    }
            }
    }

    private fun validateAppleIdToken(idToken: String): Map<String, Any> {
        val claims = verifyIdToken(idToken)

        claims["email"] as? String
            ?: throw AuthException(ErrorStatus.MISSING_EMAIL_CLAIM)

        if (claims["aud"] as? String != appleProperties.clientId) {
            throw AuthException(ErrorStatus.INVALID_ID_TOKEN_AUDIENCE)
        }
        if (claims["iss"] as? String != "https://appleid.apple.com") {
            throw AuthException(ErrorStatus.INVALID_ID_TOKEN_ISSUER)
        }
        return claims
    }

    private fun verifyIdToken(idToken: String): Map<String, Any> {
        return jwtDecoder.decode(idToken).claims
    }

    private fun createUser(email: String): Mono<R2dbcUser> {
        return userRepository.save(
            R2dbcUser(
                email = email,
                profileImage = s3Properties.s3.defaultProfileImageUrl,
            ),
        )
    }

    private fun issueNewToken(email: String, deviceId: String): Mono<Pair<String, String>> {
        val now = Instant.now()
        val accessToken = jwtProvider.createAccessToken(now, email, deviceId)
        val refreshToken = jwtProvider.createRefreshToken(now, email, deviceId)
        return refreshTokenService.upsertRefreshToken(email, deviceId, refreshToken)
            .then(refreshTokenService.removeOldestTokenIfLimitExceeded(email))
            .map { refreshToken to accessToken }
    }
}
