package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.application.global.auth.util.JwtUtil
import nmnb.application.global.infrastructure.oauth.OAuthClientComposite
import nmnb.domain.auth.SocialType
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val oAuthClientComposite: OAuthClientComposite,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
) : AuthService {

    override fun signInWithSocial(accessCode: String, type: SocialType): AuthUserResponse {
        val profile = oAuthClientComposite.getClient(type).requestProfile(accessCode)
        val email = profile.getEmail()

        userRepository.findByEmail(email) ?: userRepository.save(User(email))

        val accessToken = jwtUtil.createAccessToken(email)
        val refreshToken = jwtUtil.createRefreshToken(email)

        return AuthUserResponse(email, accessToken = accessToken, refreshToken = refreshToken)
    }
}
