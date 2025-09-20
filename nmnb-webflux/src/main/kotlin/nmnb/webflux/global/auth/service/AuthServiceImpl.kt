package nmnb.webflux.global.auth.service

import nmnb.common.properties.S3Properties
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.r2dbc.user.CustomIdGenerator
import nmnb.r2dbc.user.R2dbcUser
import nmnb.r2dbc.user.R2dbcUserRepository
import nmnb.webflux.global.auth.service.dto.request.AppleLoginServiceRequest
import nmnb.webflux.global.auth.service.dto.response.AuthUserResponse
import nmnb.webflux.global.infrastructure.security.JwtProvider
import nmnb.webflux.global.properties.AppleProperties
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant

@Service
class AuthServiceImpl(
    private val jwtProvider: JwtProvider,
    private val appleProperties: AppleProperties,
    private val s3Properties: S3Properties,
    private val jwtDecoder: JwtDecoder,
    private val userRepository: R2dbcUserRepository,
    private val refreshTokenService: RefreshTokenService,
    transactionalManager: ReactiveTransactionManager,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : AuthService {

    private val txOperator = TransactionalOperator.create(transactionalManager)

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

        val aud = claims["aud"]
        if (aud is Array<*>) {
            if (!aud.contains(appleProperties.clientId)) {
                throw AuthException(ErrorStatus.INVALID_ID_TOKEN_AUDIENCE)
            }
        } else if (aud is String) {
            if (aud != appleProperties.clientId) {
                throw AuthException(ErrorStatus.INVALID_ID_TOKEN_AUDIENCE)
            }
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
        val newUser = R2dbcUser(
            id = CustomIdGenerator.generateId(),
            email = email,
            profileImage = s3Properties.s3.defaultProfileImageUrl,
        )
        return r2dbcEntityTemplate.insert(newUser)
    }

    private fun issueNewToken(email: String, deviceId: String): Mono<Pair<String, String>> {
        val now = Instant.now()
        val accessToken = jwtProvider.createAccessToken(now, email, deviceId)
        val refreshTokenMono = jwtProvider.createRefreshToken(now, email, deviceId)
            .subscribeOn(Schedulers.boundedElastic())

        return refreshTokenMono.flatMap { refreshToken ->
            refreshTokenService
                .upsertRefreshToken(email, deviceId, refreshToken)
                .then(refreshTokenService.removeOldestTokenIfLimitExceeded(email))
                .thenReturn(Pair(refreshToken, accessToken))
        }.subscribeOn(Schedulers.boundedElastic())
    }
}
