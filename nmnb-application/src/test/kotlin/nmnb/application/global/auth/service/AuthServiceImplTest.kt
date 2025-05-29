package nmnb.application.global.auth.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.global.auth.service.dto.KakaoAccount
import nmnb.application.global.auth.service.dto.KakaoProfile
import nmnb.application.global.infrastructure.external.KakaoOAuthClient
import nmnb.application.global.infrastructure.external.OAuthClientComposite
import nmnb.common.domain.SignUpStatus
import nmnb.domain.auth.SocialType
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

class AuthServiceImplTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var kakaoOAuthClient: KakaoOAuthClient

    @MockBean
    private lateinit var oAuthClientComposite: OAuthClientComposite

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

        userRepository.save(
            User(email = email, profileImage = profileImage),
        )

        val kakaoProfile = KakaoProfile(kakaoAccount = KakaoAccount(email = email))
        whenever(oAuthClientComposite.getClient(SocialType.KAKAO)).thenReturn(kakaoOAuthClient)
        whenever(kakaoOAuthClient.requestProfile(accessToken)).thenReturn(kakaoProfile)

        // when
        val result = authService.signInWithSocial(accessToken, SocialType.KAKAO)

        // then
        assertThat(userRepository.findAll()).hasSize(1)
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.accessToken).isNotBlank()
        assertThat(result.refreshToken).isNotBlank()
    }

    @Test
    @DisplayName("신규 유저일 경우, 소셜 로그인 시 유저가 저장되고 토큰이 발급된다")
    fun signUpWithSocial() {
        // given
        val email = "test@example.com"
        val accessToken = "dummy-access-token"

        val kakaoProfile = KakaoProfile(kakaoAccount = KakaoAccount(email = email))
        whenever(oAuthClientComposite.getClient(SocialType.KAKAO)).thenReturn(kakaoOAuthClient)
        whenever(kakaoOAuthClient.requestProfile(accessToken)).thenReturn(kakaoProfile)

        // when
        val result = authService.signInWithSocial(accessToken, SocialType.KAKAO)

        // then
        assertThat(userRepository.findAll()).hasSize(1)
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.accessToken).isNotBlank()
        assertThat(result.refreshToken).isNotBlank()
        assertThat(result.signUpStatus).isEqualTo(SignUpStatus.IN_PROGRESS)
    }
}
