package nmnb.application.domain.post.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.post.service.dto.response.PostPageResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PostControllerTest : ControllerTestSupport() {
    @DisplayName("게시글을 랜덤으로 조회하는데 성공한다.")
    @ParameterizedTest(name = "{index} : authentication = {0}")
    @CsvSource(
        "true, access.token, 1234", // 인증 사용자
        "false, , ", // 비인증 사용자
    )
    fun getPostPage(authentication: Boolean, accessToken: String?, deviceId: String?) {
        // given
        val user = if (authentication) User.fixture() else null
        if (authentication) {
            mockUserAuthentication(user!!, accessToken!!, deviceId!!)
        }

        // when
        whenever(postService.getPostPage(anyOrNull(), any()))
            .thenReturn(
                PostPageResponse(
                    listOf(),
                    false,
                    1,
                ),
            )

        // then
        val requestBuilder = get("/v1/api/videos")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .param("seed", "1234")
            .param("cursor", "-1")
            .param("size", "2")

        if (authentication) {
            requestBuilder
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
        }

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.postInfo").isArray)
            .andExpect(jsonPath("$.result.hasNext").value(false))
            .andExpect(jsonPath("$.result.nextCursor").value(1))
    }

    @WithMockUser
    @DisplayName("게시글 삭제 요청에 성공한다.")
    @Test
    fun deletePost() {
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)

        doNothing().whenever(postService).deletePost(any(), anyLong())

        mockMvc.perform(
            delete("/v1/api/videos/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),

        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.NO_CONTENT.code))
    }

    @WithMockUser
    @DisplayName("내 게시글 조회 요청에 성공한다.")
    @Test
    fun getMyPost() {
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()

        mockUserAuthentication(user, accessToken, deviceId)

        doNothing().whenever(postService).deletePost(any(), anyLong())

        mockMvc.perform(
            get("/v1/api/users/me/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
    }
}
