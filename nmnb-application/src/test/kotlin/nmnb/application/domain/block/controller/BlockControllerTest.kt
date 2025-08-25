package nmnb.application.domain.block.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.block.controller.dto.request.UserBlockRequest
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class BlockControllerTest : ControllerTestSupport() {

    @WithMockUser
    @DisplayName("차단 요청에 성공한다")
    @Test
    fun block() {
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)
        val request = UserBlockRequest("")

        doNothing().whenever(userBlockService).block(any(), any())

        mockMvc.perform(
            post("/v1/api/users/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf())
                .content(
                    objectMapper.writeValueAsString(request),
                ),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(SuccessStatus.OK.code))
    }

    @WithMockUser
    @DisplayName("차단 해제 요청에 성공한다")
    @Test
    fun unBlock() {
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)
        val request = UserBlockRequest("")

        doNothing().whenever(userBlockService).unBlock(any(), any())

        mockMvc.perform(
            delete("/v1/api/users/blocks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf())
                .content(
                    objectMapper.writeValueAsString(request),
                ),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(SuccessStatus.OK.code))
    }
}
