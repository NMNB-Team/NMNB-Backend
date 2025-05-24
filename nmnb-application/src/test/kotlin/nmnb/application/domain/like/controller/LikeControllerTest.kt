package nmnb.application.domain.like.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.like.service.dto.request.PostLikeServiceRequest
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LikeControllerTest : ControllerTestSupport() {

    @BeforeEach
    fun setup() {
        val user = User(
            email = "test@example.com",
            profileImage = "profile",
        )
        given(userRepository.findByEmail(any())).willReturn(user)
    }


    @WithMockUser
    @DisplayName("좋아요가 등록되어있으면 취소, 좋아요가 없으면 등록하는 것에 성공한다.")
    @Test
    fun likeOrUnlike() {
        // given
        val request = PostLikeServiceRequest(1L)

        // when & then
        doNothing().whenever(likeService).likeOrUnlike(any(), any())

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/v1/api/like")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(
                    objectMapper.writeValueAsString(request),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
    }
}
