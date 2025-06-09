package nmnb.application.global.auth.service

import nmnb.application.global.infrastructure.security.JwtTokenProvider
import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.auth.RefreshToken
import nmnb.domain.auth.repository.RefreshTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenProvider: JwtTokenProvider,
) {
    fun validateRefreshToken(refreshToken: String): String {
        val email = tokenProvider.getEmailWithValidation(refreshToken)
        verifyStoredTokenMatch(email, refreshToken)
        return email
    }

    fun verifyStoredTokenMatch(email: String, token: String) {
        val storedToken = refreshTokenRepository.findByIdOrNull(email)?.refreshToken

        if (storedToken != token) {
            throw AuthException(ErrorStatus.AUTH_INVALID_TOKEN)
        }
    }

    fun removeOldestIfSessionLimitExceeded(email: String) {
        val allTokens = getUserRefreshTokens(email)
        if (allTokens.size >= MAX_SESSIONS) {
            val oldest = findOldestToken(allTokens)
            if (oldest != null) {
                refreshTokenRepository.deleteById(oldest.id)
            }
        }
    }

    private fun findOldestToken(allTokens: List<RefreshToken>) =
        allTokens.minByOrNull { it.timeStamp }

    private fun getUserRefreshTokens(email: String) = refreshTokenRepository.findAll().filter { it.email == email }

    companion object {
        const val MAX_SESSIONS = 4
    }
}
