package nmnb.application.global.auth.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.common.utils.JwtConstants.DEVICE_ID_CLAIM_KEY
import nmnb.domain.user.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerTest() : ControllerTestSupport() {
    @DisplayName("정상적으로 토큰을 재발급받는다")
    @WithMockUser
    @Test
    fun refreshToken() {
        // given
        val dummyRefreshToken = "dummy.refresh.token"
        val dummyDeviceId = "deviceId"

        val expectedResponse = AuthTokenResponse(
            accessToken = "new.access.token",
            refreshToken = "new.refresh.token",
        )

        whenever(authService.refreshToken(dummyRefreshToken, dummyDeviceId)).thenReturn(expectedResponse)

        // when & then
        mockMvc.perform(
            get("/v1/api/auth/refresh")
                .header("X-Refresh-Token", dummyRefreshToken)
                .header("Device-Id", dummyDeviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.accessToken").value("new.access.token"))
            .andExpect(jsonPath("$.result.refreshToken").value("new.refresh.token"))
    }

    @DisplayName("로그아웃에 성공한다.")
    @WithMockUser
    @Test
    fun logout() {
        // given
        val refreshToken = "refresh.token"
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)
        whenever(authService.logout(any(), any(), any(), any())).then { }

        // when & then
        mockMvc.perform(
            post("/v1/api/auth/logout")
                .header("X-Refresh-Token", refreshToken)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
    }

    private fun mockUserAuthentication(user: User, accessToken: String, deviceId: String) {
        whenever(jwtProvider.getEmailWithValidation(accessToken)).thenReturn(user.email)
        whenever(userRepository.findByEmail(user.email)).thenReturn(user)
        whenever(jwtProvider.getClaimFromToken(accessToken, DEVICE_ID_CLAIM_KEY)).thenReturn(deviceId)
    }
}
