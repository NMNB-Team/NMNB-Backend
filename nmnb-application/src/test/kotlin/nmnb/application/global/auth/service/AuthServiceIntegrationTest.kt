package nmnb.application.global.auth.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.global.auth.util.JwtTokenProvider
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test

class AuthServiceIntegrationTest : IntegrationTestSupport() {

    @Autowired
    lateinit var authService: AuthServiceImpl

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    lateinit var refreshToken: String

    @BeforeEach
    fun setUp() {
        val email = "test@example.com"
        refreshToken = tokenProvider.createRefreshToken(email)
        refreshTokenRepository.save(RefreshToken(email, refreshToken))
    }

    @DisplayName("refreshToken은 실제 토큰을 받아 새로운 토큰을 발급한다")
    @Test
    fun refreshToken() {
        // when
        val result = authService.refreshToken(refreshToken)

        // then
        assertThat(result.accessToken).isNotBlank()
        assertThat(result.refreshToken).isNotBlank()
    }
}
