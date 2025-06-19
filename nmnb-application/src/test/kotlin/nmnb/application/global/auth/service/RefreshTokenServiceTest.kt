package nmnb.application.global.auth.service

import nmnb.application.global.common.utils.DeviceIdUtils
import nmnb.application.global.infrastructure.security.JwtProvider
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.Optional

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class RefreshTokenServiceTest {
    @InjectMocks
    private lateinit var refreshTokenService: RefreshTokenService

    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Mock
    private lateinit var jwtProvider: JwtProvider

    @Test
    @DisplayName("한 계정에 저장된 RefreshToken 수가 제한보다 작으면 아무 것도 삭제하지 않는다")
    fun removeOldestIfSessionLimitExceeded() {
        // given
        val email = "email@email.com"
        val now = LocalDateTime.now()
        val tokens = listOf(
            RefreshToken.fixture(email, "refreshToken1", now.minusSeconds(50), "device1"),
            RefreshToken.fixture(email, "refreshToken2", now.minusSeconds(40), "device2"),
            RefreshToken.fixture(email, "refreshToken3", now.minusSeconds(30), "device3"),
            RefreshToken.fixture(email, "refreshToken4", now.minusSeconds(20), "device4"),
        )
        whenever(refreshTokenRepository.findAll()).thenReturn(tokens)

        // when
        refreshTokenService.removeOldestTokenIfLimitExceeded(email)

        // then
        verify(refreshTokenRepository, never()).deleteById(any())
    }

    @Test
    @DisplayName("한 계정에 5개 이상의 RefreshToken이 저장될 때, 4개의 RefreshToken만 남겨두고 삭제된다. ")
    fun removeOldestTokenIfLimitExceededWhen5TokenSaved() {
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
        refreshTokenService.removeOldestTokenIfLimitExceeded(email)

        // then
        verify(refreshTokenRepository, times(1)).deleteById("$email:device1")
        verify(refreshTokenRepository, times(1)).deleteById(any())
    }

    @Test
    @DisplayName("저장되어있던 RefreshToken이 있다면 해당 값이 업데이트 된다.")
    fun upsertRefreshTokenWhenExistRefreshToken() {
        // given
        val email = "email@email.com"
        val deviceId = "device1"
        val tokenId = DeviceIdUtils.deviceIdFormatter(email, deviceId)
        val oldToken = RefreshToken(
            id = tokenId,
            email = email,
            deviceId = deviceId,
            refreshToken = "oldToken",
            timeStamp = LocalDateTime.now().minusDays(1),
        )
        val oldTimeStamp = oldToken.timeStamp

        val newRefreshToken = "newRefreshToken"
        whenever(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.of(oldToken))

        // when
        refreshTokenService.upsertRefreshToken(email, deviceId, newRefreshToken)

        // then
        val captor = argumentCaptor<RefreshToken>()
        verify(refreshTokenRepository).save(captor.capture())

        val savedToken = captor.firstValue
        assertEquals(tokenId, savedToken.id)
        assertEquals(email, savedToken.email)
        assertEquals(deviceId, savedToken.deviceId)
        assertEquals(newRefreshToken, savedToken.refreshToken)
        assertTrue(savedToken.timeStamp.isAfter(oldTimeStamp))
    }

    @Test
    @DisplayName("저장되어있던 RefreshToken이 없다면 새 토큰이 저장된다.")
    fun upsertRefreshToken() {
        // given
        val email = "email@email.com"
        val deviceId = "device1"
        val tokenId = DeviceIdUtils.deviceIdFormatter(email, deviceId)

        val newRefreshToken = "newRefreshToken"

        whenever(refreshTokenRepository.findById(tokenId)).thenReturn(Optional.empty())

        // when
        refreshTokenService.upsertRefreshToken(email, deviceId, newRefreshToken)

        // then
        val captor = argumentCaptor<RefreshToken>()
        verify(refreshTokenRepository).save(captor.capture())

        val savedToken = captor.firstValue
        assertEquals(tokenId, savedToken.id)
        assertEquals(email, savedToken.email)
        assertEquals(deviceId, savedToken.deviceId)
        assertEquals(newRefreshToken, savedToken.refreshToken)
        assertThat(savedToken.timeStamp).isNotNull
    }

    @Test
    @DisplayName("RefreshToken이 유효하면 저장소에서 삭제한다")
    fun deleteRefreshToken() {
        // given
        val email = "email@email.com"
        val deviceId = "device1"
        val refreshToken = "refresh-token"
        val id = DeviceIdUtils.deviceIdFormatter(email, deviceId)
        val token = RefreshToken.fixture(email, refreshToken, LocalDateTime.now(), deviceId)

        whenever(jwtProvider.getEmailWithValidation(any())).thenReturn(email)
        whenever(refreshTokenRepository.findById(id)).thenReturn(Optional.of(token))

        // when
        refreshTokenService.deleteRefreshToken(id, refreshToken)

        // then
        verify(refreshTokenRepository).deleteById(id)
    }
}
