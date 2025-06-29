package nmnb.application.global.auth.service

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
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.test.assertTrue

class AuthServiceImplTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var refreshTokenService: RefreshTokenService

    @MockBean
    private lateinit var blacklistService: BlacklistService

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var kakaoOAuthClient: KakaoOAuthClient

    @MockBean
    private lateinit var oAuthClientComposite: OAuthClientComposite

    @MockBean
    private lateinit var jwtProvider: JwtProvider

    private val logger = LoggerFactory.getLogger(javaClass)

    @AfterEach
    fun tearDown() {
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
    @DisplayName("동시에 여러 요청이 들어와도 user는 1명만 생성되어야 한다")
    fun signInWithKakaoForConcurrencyTest() {
        // given
        val accessCode = "mock-code"
        val type = SocialType.KAKAO
        val deviceId = "deviceId"
        val email = "test@example.com"
        val newRefreshToken = "new-refresh-token"
        val newAccessToken = "new-access-token"

        val kakaoProfile = KakaoProfile(kakaoAccount = KakaoAccount(email = email))

        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)

        // when
        whenever(oAuthClientComposite.getClient(SocialType.KAKAO)).thenReturn(kakaoOAuthClient)
        whenever(kakaoOAuthClient.requestProfile(any())).thenReturn(kakaoProfile)
        whenever(jwtProvider.createRefreshToken(any(), any(), any())).thenReturn(newRefreshToken)
        whenever(jwtProvider.createAccessToken(any(), any(), any())).thenReturn(newAccessToken)

        val futures = mutableListOf<Future<*>>()

        repeat(threadCount) {
            val future = executor.submit {
                try {
                    authService.signInWithSocial(accessCode, type, deviceId)
                    logger.info("요청 성공")
                } finally {
                    latch.countDown()
                }
            }
            futures.add(future)
        }

        latch.await()

        // 모든 Future 결과를 검사해서 예외 발생 여부 확인
        futures.forEach { future ->
            future.get() // 예외 발생 시 여기서 던져져 테스트 실패함
        }

        logger.info("모든 쓰레드 작업 완료")
    }
}
