package nmnb.application.domain.user.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationRequest
import nmnb.application.domain.user.service.dto.response.UserPetRegistrationResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.PetOwnershipStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
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
                    PetOwnershipStatus.HAS_PET,
                    "2025.01.02",
                ),
            )

        // when & then
        mockMvc.perform(
            get("/v1/api/users/profile")
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
            .andExpect(jsonPath("$.result.petOwnershipStatus").value("HAS_PET"))
            .andExpect(jsonPath("$.result.createdAt").isNotEmpty)
    }

    @WithMockUser
    @DisplayName("반려견 이름 등록에 성공한다")
    @Test
    fun registerPet() {
        // given
        val request = UserPetRegistrationRequest(petName = "멍멍이")
        whenever(userService.registerPet(any(), any()))
            .thenReturn(
                UserPetRegistrationResponse(
                    petName = "멍멍이",
                    petOwnershipStatus = PetOwnershipStatus.HAS_PET,
                ),
            )

        // when & then
        mockMvc.perform(
            patch("/v1/api/users/pet")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(
                    objectMapper.writeValueAsString(request),
                ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.petName").value("멍멍이"))
            .andExpect(jsonPath("$.result.petOwnershipStatus").value("HAS_PET"))
    }
}
