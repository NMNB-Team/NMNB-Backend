package nmnb.application.global.auth.test

import nmnb.application.global.auth.test.dto.TestAuthResponse
import nmnb.application.global.infrastructure.security.JwtProvider
import nmnb.common.response.exception.UserException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TestAuthService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {

    @Transactional
    fun login(email: String, deviceId: String): TestAuthResponse {
        val user: User =
            userRepository
                .findByEmail(email) ?: throw UserException(ErrorStatus.USER_NOT_FOUND)

        val now = Instant.now()
        val accessToken: String = jwtProvider.createAccessToken(now, user.email, deviceId)
        return TestAuthResponse(accessToken)
    }
}
