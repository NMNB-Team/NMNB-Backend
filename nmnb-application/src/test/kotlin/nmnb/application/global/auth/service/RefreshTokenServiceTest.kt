package nmnb.application.global.auth.service

import nmnb.application.IntegrationTestSupport
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDateTime

class RefreshTokenServiceTest : IntegrationTestSupport() {
    @Autowired
    private lateinit var refreshTokenService: RefreshTokenService

    @MockBean
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Test
    @DisplayName("한 계정에 저장된 RefreshToken 수가 제한보다 작으면 아무 것도 삭제하지 않는다")
    fun removeOldestIfSessionLimitExceeded() {
        // given
        val email = "email@email.com"
        val now = LocalDateTime.now()
        val tokens = listOf(
            RefreshToken.fixture(email, "refreshToken1", now.minusSeconds(50), "device1"),
            RefreshToken.fixture(email, "refreshToken2", now.minusSeconds(40), "device2"),
        )
        whenever(refreshTokenRepository.findAll()).thenReturn(tokens)

        // when
        refreshTokenService.removeOldestIfSessionLimitExceeded(email)

        // then
        verify(refreshTokenRepository, never()).deleteById(any())
    }

    @Test
    @DisplayName("한 계정에 4개 이상의 RefreshToken이 저장될 때, 가장 오래된 기기의 토큰이 제거된다.")
    fun removeOldestIfSessionLimitExceededIfMoreThanMaxSessions() {
        // given
        val email = "email@email.com"
        val now = LocalDateTime.now()
        val tokens = listOf(
            RefreshToken.fixture(email, "refreshToken1", now.minusSeconds(50), "device1"),
            RefreshToken.fixture(email, "refreshToken2", now.minusSeconds(40), "device2"),
            RefreshToken.fixture(email, "refreshToken3", now.minusSeconds(30), "device3"),
            RefreshToken.fixture(email, "refreshToken4", now.minusSeconds(20), "device4"),
            RefreshToken.fixture(email, "refreshToken5", now.minusSeconds(10), "device5"),
        )
        whenever(refreshTokenRepository.findAll()).thenReturn(tokens)

        // when
        refreshTokenService.removeOldestIfSessionLimitExceeded(email)

        // then
        verify(refreshTokenRepository).deleteById("$email:device1")
    }
}
