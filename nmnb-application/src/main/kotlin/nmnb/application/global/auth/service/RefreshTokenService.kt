package nmnb.application.global.auth.service

import nmnb.application.global.common.utils.DeviceIdUtils
import nmnb.application.global.infrastructure.security.JwtProvider
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
) {
    fun validateRefreshToken(refreshToken: String, deviceId: String): String {
        val email = jwtProvider.getEmailWithValidation(refreshToken)
        val id = DeviceIdUtils.deviceIdFormatter(email, deviceId)
        verifyStoredTokenMatch(id, refreshToken)
        return email
    }

    fun verifyStoredTokenMatch(id: String, token: String) {
        val storedToken = refreshTokenRepository.findByIdOrNull(id)?.refreshToken

        if (storedToken != token) {
            throw AuthException(ErrorStatus.AUTH_INVALID_TOKEN)
        }
    }

    @Transactional
    fun upsertRefreshToken(email: String, deviceId: String, refreshToken: String) {
        val tokenId = "$email:$deviceId"
        val token = refreshTokenRepository.findByIdOrNull(tokenId)

        val now = LocalDateTime.now()

        if (token != null) {
            token.update(
                refreshToken = refreshToken,
                timeStamp = now,
            )
            refreshTokenRepository.save(token)
        } else {
            val newToken = RefreshToken(
                id = tokenId,
                email = email,
                deviceId = deviceId,
                refreshToken = refreshToken,
                timeStamp = now,
            )
            refreshTokenRepository.save(newToken)
        }
    }

    @Transactional
    fun removeOldestTokenIfLimitExceeded(email: String) {
        val allTokens = getUserRefreshTokensSortedByTime(email)

        if (allTokens.size <= MAX_REFRESH_TOKENS) return

        val tokensToRemove = allTokens.dropLast(MAX_REFRESH_TOKENS)
        tokensToRemove.forEach { refreshTokenRepository.deleteById(it.id) }
    }

    private fun getUserRefreshTokensSortedByTime(email: String) =
        refreshTokenRepository.findAll().filter { it.email == email }
            .sortedBy { it.timeStamp }
            .toMutableList()

    companion object {
        const val MAX_REFRESH_TOKENS = 4
    }
}
