package nmnb.application.global.auth.service

import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.application.global.auth.util.JwtUtil
import nmnb.application.global.auth.util.KakaoUtil
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val kakaoUtil: KakaoUtil,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
) : AuthService {

    override fun signInWithSocial(accessCode: String): AuthUserResponse {
        val kakaoToken = kakaoUtil.requestToken(accessCode)
        val kakaoProfile = kakaoUtil.requestProfile(kakaoToken)
        val email = kakaoProfile.kakaoAccount.email

        userRepository.findByEmail(email) ?: userRepository.save(User(email))

        val accessToken = jwtUtil.createAccessToken(email)
        val refreshToken = jwtUtil.createRefreshToken(email)

        return AuthUserResponse(email, accessToken = accessToken, refreshToken = refreshToken)
    }
}
