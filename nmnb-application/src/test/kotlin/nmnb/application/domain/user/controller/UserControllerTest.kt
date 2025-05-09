package nmnb.application.domain.user.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
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

class UserControllerTest() : ControllerTestSupport() {

    @WithMockUser
    @DisplayName("사용자 마이페이지 조회에 성공한다")
    @Test
    fun getProfile() {
        // given
        whenever(userService.getProfile(any()))
            .thenReturn(
                UserProfileResponse(
                    "nickname",
                    "profile",
                    true,
                    "2025.01.02",
                ),
            )

        // when & then
        mockMvc.perform(
            get("/v1/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(
                    """
                {
                    "email": "test@example.com",
                    "profileImage": "profile_image.png",
                    "petName": "dog",
                    "petOwnershipStatus": "HAS_PET"
                }
                """,
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.nickName").value("nickname"))
            .andExpect(jsonPath("$.result.profileImage").value("profile"))
            .andExpect(jsonPath("$.result.hasAnimal").value(true))
            .andExpect(jsonPath("$.result.createdAt").isNotEmpty)
    }
}
