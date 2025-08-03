package nmnb.application.global.auth.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import nmnb.application.IntegrationTestSupport
import nmnb.application.global.auth.service.dto.KakaoAccount
import nmnb.application.global.auth.service.dto.KakaoProfile
import nmnb.application.global.common.utils.DeviceIdUtils
import nmnb.application.global.infrastructure.external.oauth.KakaoOAuthClient
import nmnb.application.global.infrastructure.external.oauth.OAuthClientComposite
import nmnb.application.global.infrastructure.security.BlacklistService
import nmnb.application.global.infrastructure.security.JwtProvider
import nmnb.common.domain.SignUpStatus
import nmnb.domain.auth.SocialType
import nmnb.domain.like.Like
import nmnb.domain.like.repository.LikeRepository
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.user.User
import nmnb.domain.user.WithdrawType
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.transaction.annotation.Transactional

class AuthServiceImplTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var refreshTokenService: RefreshTokenService

    @MockBean
    private lateinit var blacklistService: BlacklistService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var likeRepository: LikeRepository

    @MockBean
    private lateinit var kakaoOAuthClient: KakaoOAuthClient

    @MockBean
    private lateinit var oAuthClientComposite: OAuthClientComposite

    @MockBean
    private lateinit var jwtProvider: JwtProvider

    @PersistenceContext
    private lateinit var em: EntityManager

    @AfterEach
    fun tearDown() {
        likeRepository.deleteAllInBatch()
        postRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @Test
    @DisplayName("기존 유저가 존재할 경우, 소셜 로그인 시 토큰이 정상 발급된다")
    fun signInWithKakao() {
        // given
        val email = "test@example.com"
        val accessToken = "dummy-access-token"
        val profileImage = "default.png"
        val deviceId = "deviceId"
        val newRefreshToken = "new-refresh-token"
        val newAccessToken = "new-access-token"

        userRepository.save(
            User(email = email, profileImage = profileImage),
        )

        val kakaoProfile = KakaoProfile(kakaoAccount = KakaoAccount(email = email))
        whenever(oAuthClientComposite.getClient(SocialType.KAKAO)).thenReturn(kakaoOAuthClient)
        whenever(kakaoOAuthClient.requestProfile(accessToken)).thenReturn(kakaoProfile)
        whenever(jwtProvider.createRefreshToken(any(), any(), any())).thenReturn(newRefreshToken)
        whenever(jwtProvider.createAccessToken(any(), any(), any())).thenReturn(newAccessToken)

        // when
        val result = authService.signInWithSocial(accessToken, SocialType.KAKAO, deviceId)

        // then
        assertThat(userRepository.findAll()).hasSize(1)
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.accessToken).isNotBlank
        assertThat(result.refreshToken).isNotBlank
    }

    @Test
    @DisplayName("신규 유저일 경우, 소셜 로그인 시 유저가 저장되고 토큰이 발급된다")
    fun signUpWithSocial() {
        // given
        val email = "test@example.com"
        val accessToken = "dummy-access-token"
        val deviceId = "deviceId"
        val newRefreshToken = "new-refresh-token"
        val newAccessToken = "new-access-token"

        val kakaoProfile = KakaoProfile(kakaoAccount = KakaoAccount(email = email))
        whenever(oAuthClientComposite.getClient(SocialType.KAKAO)).thenReturn(kakaoOAuthClient)
        whenever(kakaoOAuthClient.requestProfile(any())).thenReturn(kakaoProfile)
        whenever(jwtProvider.createRefreshToken(any(), any(), any())).thenReturn(newRefreshToken)
        whenever(jwtProvider.createAccessToken(any(), any(), any())).thenReturn(newAccessToken)

        // when
        val result = authService.signInWithSocial(accessToken, SocialType.KAKAO, deviceId)

        // then
        assertThat(userRepository.findAll()).hasSize(1)
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.accessToken).isNotBlank
        assertThat(result.refreshToken).isNotBlank
        assertThat(result.signUpStatus).isEqualTo(SignUpStatus.IN_PROGRESS)
    }

    @Test
    @DisplayName("RefreshToken 재발급에 성공한다.")
    fun refreshToken() {
        // given
        val deviceId = "deviceId"
        val email = "user@example.com"
        val refreshToken = "valid-refresh-token"
        val newRefreshToken = "new-refresh-token"
        val newAccessToken = "new-access-token"

        whenever(refreshTokenService.validateRefreshToken(refreshToken, deviceId)).thenReturn(email)
        whenever(refreshTokenService.removeOldestTokenIfLimitExceeded(any())).then {}
        whenever(jwtProvider.createRefreshToken(any(), any(), any())).thenReturn(newRefreshToken)
        whenever(jwtProvider.createAccessToken(any(), any(), any())).thenReturn(newAccessToken)

        // when
        val result = authService.refreshToken(refreshToken, deviceId)

        // then
        assertEquals(newAccessToken, result.accessToken)
        assertEquals(newRefreshToken, result.refreshToken)
    }

    @Test
    @DisplayName("로그아웃에 성공한다.")
    fun logout() {
        // given
        val user = User.fixture()
        val deviceId = "deviceId"
        val id = DeviceIdUtils.formatDeviceId(user, deviceId)
        val refreshToken = "refresh-token"
        val accessToken = "access-token"

        whenever(refreshTokenService.deleteRefreshToken(id, deviceId)).then {}
        whenever(blacklistService.register(any())).then {}

        // when
        authService.logout(user, deviceId, accessToken, refreshToken)

        // then
        verify(refreshTokenService).deleteRefreshToken(id, refreshToken)
        verify(blacklistService).register(accessToken)
    }

    @Test
    @DisplayName("회원탈퇴에 성공한다. soft delete 되어 데이터는 남아있지만, 조회에서는 제외된다. ")
    fun withdraw() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        // when
        authService.withdraw(user)

        // then
        val deleteValue = em.createNativeQuery("SELECT deleted FROM users u WHERE u.user_id = ?")
            .setParameter(1, user.id)
            .singleResult
        assertThat(deleteValue).isEqualTo(true)

        assertThat(userRepository.findByEmail(user.email)).isNull()
    }

    @Test
    @Transactional
    @DisplayName("회원 탈퇴 시 유저가 데이터베이스에서 삭제된다. ")
    fun withdrawWithHardDelete_removeUser() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        // when
        authService.withdraw(user, withdrawType = WithdrawType.HARD)

        em.flush()
        em.clear()

        // then
        assertThat(userRepository.findById(user.id!!)).isEmpty
    }

    @Test
    @Transactional
    @DisplayName("회원 탈퇴 시 유저가 게시한 영상은 데이터베이스에서 삭제된다. ")
    fun withdrawWithHardDelete_removeUserPosts() {
        // given
        val user1 = User.fixture()
        val user2 = User.fixture()
        userRepository.saveAll(listOf(user1, user2))

        val post1 = Post.fixture(user = user1)
        val post2 = Post.fixture(user = user1)
        val post3 = Post.fixture(user = user2)
        postRepository.saveAll(listOf(post1, post2, post3))

        // when
        authService.withdraw(user1, withdrawType = WithdrawType.HARD)

        em.flush()
        em.clear()

        // then
        val posts = em.createQuery("SELECT p FROM Post p WHERE p.user.id = :id", Post::class.java)
            .setParameter("id", user1.id)
            .resultList
        assertThat(posts).isEmpty()

        assertThat(postRepository.findById(post1.id!!)).isEmpty
        assertThat(postRepository.findById(post3.id!!)).isPresent
    }

    @Test
    @Transactional
    @DisplayName("회원 탈퇴 시 유저가 게시글에 누른 좋아요는 데이터베이스에서 삭제된다. ")
    fun withdrawWithHardDelete_removeUserLikes() {
        // given
        val user1 = User.fixture()
        val user2 = User.fixture()
        userRepository.saveAll(listOf(user1, user2))

        val post1 = Post.fixture(user = user1)
        val post2 = Post.fixture(user = user1)
        val post3 = Post.fixture(user = user2)
        postRepository.saveAll(listOf(post1, post2, post3))

        likeRepository.saveAll(
            listOf(
                Like.fixture(user1, post1),
                Like.fixture(user1, post2),
                Like.fixture(user1, post3),
                Like.fixture(user2, post1),
                Like.fixture(user2, post3),
            ),
        )

        // when
        authService.withdraw(user1, withdrawType = WithdrawType.HARD)

        // then
        val likes = em.createNativeQuery("SELECT * FROM user_post_likes l WHERE l.user_id = :id")
            .setParameter("id", user1.id)
            .resultList
        assertThat(likes).isEmpty()

        assertThat(likeRepository.findAll().size).isEqualTo(1)
    }
}
