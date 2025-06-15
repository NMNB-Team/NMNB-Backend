package nmnb.application.global.auth.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.global.auth.generator.ExtractDeviceIdArgumentResolver
import nmnb.application.global.auth.generator.ExtractTokenArgumentResolver
import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.common.response.status.SuccessStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest() : ControllerTestSupport() {

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(AuthController(authService))
            .setCustomArgumentResolvers(
                ExtractTokenArgumentResolver(),
                ExtractDeviceIdArgumentResolver(),
            )
            .build()
    }

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
                .header("Authorization", "Bearer $dummyRefreshToken")
                .header("Device-Id", dummyDeviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.accessToken").value("new.access.token"))
            .andExpect(jsonPath("$.result.refreshToken").value("new.refresh.token"))
    }
}
