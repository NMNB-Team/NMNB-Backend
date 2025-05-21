package nmnb.application.global.auth.service

import nmnb.application.global.auth.exception.AuthException
import nmnb.application.global.auth.util.JwtTokenProvider
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

    fun saveOrUpdateToken(email: String, refreshToken: String) {
        val existingToken = refreshTokenRepository.findByIdOrNull(email)
        if (existingToken != null) {
            existingToken.update(refreshToken)
        } else {
            refreshTokenRepository.save(RefreshToken(email, refreshToken))
        }
    }
}
