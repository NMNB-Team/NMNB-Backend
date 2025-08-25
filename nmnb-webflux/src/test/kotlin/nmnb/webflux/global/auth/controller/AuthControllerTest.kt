package nmnb.webflux.global.auth.controller

import nmnb.common.domain.SignUpStatus
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.webflux.ControllerTestSupport
import nmnb.webflux.global.auth.controller.dto.request.AppleLoginRequest
import nmnb.webflux.global.auth.service.dto.response.AuthUserResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import reactor.core.publisher.Mono

class AuthControllerTest : ControllerTestSupport() {
    @DisplayName("애플 로그인 요청에 성공한다.")
    @Test
    fun appleLogin() {
        // given
        val deviceId = "deviceId"
        val request = AppleLoginRequest("test.id.token")
        val expectedResponse = AuthUserResponse("email", "accessToken", "refreshToken", SignUpStatus.IN_PROGRESS)

        whenever(authService.appleLogin(any(), anyString())).thenReturn(Mono.just(expectedResponse))

        // when & then
        webTestClient.post()
            .uri("/netty/v1/api/auth/login/apple")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Device-Id", deviceId)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody(BaseResponse::class.java)
            .value { response ->
                assertThat(response.code).isEqualTo(SuccessStatus.OK.code)
                assertThat(response.message).isEqualTo(SuccessStatus.OK.message)
            }

        verify(authService, times(1)).appleLogin(any(), any())
    }
}
