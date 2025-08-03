package nmnb.application.global.auth.service

import jakarta.persistence.EntityManager
import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.application.global.common.utils.DeviceIdUtils
import nmnb.application.global.infrastructure.external.oauth.OAuthClientComposite
import nmnb.application.global.infrastructure.security.BlacklistService
import nmnb.application.global.infrastructure.security.JwtProvider
import nmnb.common.properties.S3Properties
import nmnb.domain.auth.SocialType
import nmnb.domain.user.User
import nmnb.domain.user.WithdrawType
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Transactional(readOnly = true)
@Service
class AuthServiceImpl(
    private val oAuthClientComposite: OAuthClientComposite,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val s3Properties: S3Properties,
    private val refreshTokenService: RefreshTokenService,
    private val blacklistService: BlacklistService,
    private val entityManager: EntityManager,
) : AuthService {

    @Transactional
    override fun signInWithSocial(
        accessCode: String,
        type: SocialType,
        deviceId: String,
    ): AuthUserResponse {
        val profile = oAuthClientComposite.getClient(type).requestProfile(accessCode = accessCode)
        val email = profile.getEmail()

        val user = userRepository.findByEmail(email) ?: userRepository.save(
            User(
                email,
                profileImage = s3Properties.s3.defaultProfileImageUrl,
            ),
        )

        val (refreshToken, accessToken) = issueNewToken(email, deviceId)

        return AuthUserResponse(
            email,
            accessToken = accessToken,
            refreshToken = refreshToken,
            signUpStatus = user.signUpStatus,
        )
    }

    @Transactional
    override fun refreshToken(refreshToken: String, deviceId: String): AuthTokenResponse {
        val email = refreshTokenService.validateRefreshToken(refreshToken, deviceId)

        val (newRefreshToken, newAccessToken) = issueNewToken(email, deviceId)

        return AuthTokenResponse(newAccessToken, newRefreshToken)
    }

    private fun issueNewToken(email: String, deviceId: String): Pair<String, String> {
        val now = Instant.now()
        val accessToken = jwtProvider.createAccessToken(now, email, deviceId)
        val refreshToken = jwtProvider.createRefreshToken(now, email, deviceId)
        refreshTokenService.upsertRefreshToken(email, deviceId, refreshToken)

        refreshTokenService.removeOldestTokenIfLimitExceeded(email)

        return Pair(refreshToken, accessToken)
    }

    @Transactional
    override fun logout(user: User, deviceId: String, accessToken: String, refreshToken: String) {
        val id = DeviceIdUtils.formatDeviceId(user, deviceId)
        refreshTokenService.deleteRefreshToken(id, refreshToken)

        blacklistService.register(accessToken)
    }

    @Transactional
    override fun withdraw(user: User, withdrawType: WithdrawType) {
        when (withdrawType) {
            WithdrawType.HARD -> {
                hardDeleteUser(user.id!!)
            }
            WithdrawType.SOFT -> {
                userRepository.delete(user)
            }
        }
    }

    private fun hardDeleteUser(userId: String) {
        // 유저가 누른 좋아요 삭제
        entityManager.createNativeQuery("DELETE FROM user_post_likes WHERE user_id = :id")
            .setParameter("id", userId)
            .executeUpdate()

        // 유저가 작성한 게시글에 달린 좋아요 삭제
        entityManager.createNativeQuery("DELETE FROM user_post_likes WHERE post_id IN (SELECT post_id FROM posts WHERE user_id = :id)")
            .setParameter("id", userId)
            .executeUpdate()

        // 유저가 작성한 게시글 삭제
        entityManager.createNativeQuery("DELETE FROM posts WHERE user_id = :id")
            .setParameter("id", userId)
            .executeUpdate()

        // 유저 삭제
        entityManager.createNativeQuery("DELETE FROM users WHERE user_id = :id")
            .setParameter("id", userId)
            .executeUpdate()
    }
}
