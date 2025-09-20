package nmnb.webflux.global.auth.service

import nmnb.common.auth.repository.RefreshTokenRepository
import nmnb.r2dbc.user.R2dbcUser
import nmnb.r2dbc.user.R2dbcUserRepository
import nmnb.webflux.IntegrationTestSupport
import nmnb.webflux.global.auth.service.dto.request.AppleLoginServiceRequest
import nmnb.webflux.global.auth.service.dto.response.AuthUserResponse
import nmnb.webflux.global.properties.AppleProperties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class AuthServiceImplTest : IntegrationTestSupport() {
    @Autowired
    lateinit var authService: AuthService

    @MockBean
    lateinit var userRepository: R2dbcUserRepository

    @MockBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @MockBean
    lateinit var jwtDecoder: JwtDecoder

    @MockBean
    lateinit var appleProperties: AppleProperties

    @MockBean
    lateinit var txOperator: TransactionalOperator

    private val testEmail = "test@example.com"
    private val testIdToken = "sample.id.token"

    @DisplayName("신규유저일 경우 회원가입 후 로그인에 성공한다.")
    @Test
    fun appleLoginFirst() {
        // given
        val user = R2dbcUser.fixture(email = testEmail)
        val request = AppleLoginServiceRequest(testIdToken)
        val claims = mapOf(
            "email" to testEmail,
            "aud" to appleProperties.clientId,
            "iss" to "https://appleid.apple.com",
        )

        // verifyToken 내부
        val mockJwt = mock<Jwt>()
        whenever(mockJwt.claims).thenReturn(claims)
        whenever(jwtDecoder.decode(testIdToken)).thenReturn(mockJwt)

        whenever(userRepository.findByEmail(testEmail)).thenReturn(Mono.empty())
        whenever(userRepository.save(any())).thenReturn(Mono.just(user))

        whenever(txOperator.execute<AuthUserResponse>(any())).thenAnswer { invocation ->
            val action = invocation.getArgument<Function1<Any, Mono<AuthUserResponse>>>(0)
            action.invoke(mock())
        }

        // when
        val response = authService.appleLogin(request, "1234")

        // then
        StepVerifier.create(response)
            .expectNextMatches { it.email == testEmail }
            .verifyComplete()

        verify(userRepository, times(1)).save(any())
    }

    @DisplayName("이미 등록되어있는 회원일 경우 로그인에 성공한다.")
    @Test
    fun appleLogin() {
        // given
        val user = R2dbcUser.fixture(email = testEmail)
        val request = AppleLoginServiceRequest(testIdToken)
        val claims = mapOf(
            "email" to testEmail,
            "aud" to appleProperties.clientId,
            "iss" to "https://appleid.apple.com",
        )

        // verifyToken 내부
        val mockJwt = mock<Jwt>()
        whenever(mockJwt.claims).thenReturn(claims)
        whenever(jwtDecoder.decode(testIdToken)).thenReturn(mockJwt)

        whenever(userRepository.findByEmail(testEmail)).thenReturn(Mono.just(user))

        whenever(txOperator.execute<AuthUserResponse>(any())).thenAnswer { invocation ->
            val action = invocation.getArgument<Function1<Any, Mono<AuthUserResponse>>>(0)
            action.invoke(mock())
        }

        // when
        val response = authService.appleLogin(request, "1234")

        // then
        StepVerifier.create(response)
            .expectNextMatches { it.email == testEmail }
            .verifyComplete()

        verify(userRepository, never()).save(any())
    }
}
