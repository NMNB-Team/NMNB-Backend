package nmnb.application.domain.post.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.post.service.dto.response.PostPageResponse
import nmnb.common.response.status.SuccessStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PostControllerTest : ControllerTestSupport() {
    @WithMockUser
    @DisplayName("게시글을 랜덤으로 조회하는데 성공한다.")
    @Test
    fun getPostPage() {
        // given
        // when
        whenever(postService.getPostPage(any(), any()))
            .thenReturn(
                PostPageResponse(
                    listOf(),
                    false,
                    1,
                ),
            )

        // then
        mockMvc.perform(
            get("/v1/api/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .param("seed", "1234")
                .param("cursor", "-1")
                .param("size", "2"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.postInfo").isArray)
            .andExpect(jsonPath("$.result.hasNext").value(false))
            .andExpect(jsonPath("$.result.nextCursor").value(1))
    }
}
