package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.application.global.infrastructure.external.OAuthClientComposite
import nmnb.application.global.infrastructure.security.JwtTokenProvider
import nmnb.common.properties.S3Properties
import nmnb.domain.auth.SocialType
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val oAuthClientComposite: OAuthClientComposite,
    private val userRepository: UserRepository,
    private val tokenProvider: JwtTokenProvider,
    private val s3Properties: S3Properties,
    private val refreshTokenService: RefreshTokenService,
) : AuthService {

    @Transactional
    override fun signInWithSocial(accessCode: String, type: SocialType): AuthUserResponse {
        val profile = oAuthClientComposite.getClient(type).requestProfile(accessCode = accessCode)
        val email = profile.getEmail()

        val user = userRepository.findByEmail(email) ?: userRepository.save(User(email, profileImage = s3Properties.s3.defaultProfileImageUrl))

        val accessToken = tokenProvider.createAccessToken(email)
        val refreshToken = tokenProvider.createRefreshToken(email)

        return AuthUserResponse(email, accessToken = accessToken, refreshToken = refreshToken, signUpStatus = user.signUpStatus)
    }

    @Transactional
    override fun refreshToken(refreshToken: String): AuthTokenResponse {
        val email = refreshTokenService.validateRefreshToken(refreshToken)

        val newAccessToken = tokenProvider.createAccessToken(email)
        val newRefreshToken = tokenProvider.createRefreshToken(email)

        refreshTokenService.saveOrUpdateToken(email, newRefreshToken)

        return AuthTokenResponse(newAccessToken, newRefreshToken)
    }
}
