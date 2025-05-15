package nmnb.application.domain.user.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationRequest
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.SignUpStatus
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
    @DisplayName("반려견의 이름을 등록 및 반려견 소유 상태 설정에 성공한다")
    @Test
    fun registerWithPetName() {
        // given
        val request = UserPetRegistrationRequest(petName = "멍멍이")
        whenever(userService.registerWithPetName(any(), any()))
            .thenReturn(
                UserStatusResponse(
                    nickName = "멍멍이-{랜덤값}",
                    petOwnershipStatus = PetOwnershipStatus.HAS_PET,
                    signUpStatus = SignUpStatus.COMPLETE,
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
            .andExpect(jsonPath("$.result.nickName").value("멍멍이-{랜덤값}"))
            .andExpect(jsonPath("$.result.petOwnershipStatus").value("HAS_PET"))
            .andExpect(jsonPath("$.result.signUpStatus").value("COMPLETE"))
    }

    @WithMockUser
    @DisplayName("반려견 미보유 상태 등록에 성공한다")
    @Test
    fun registerWithoutPet() {
        // given
        whenever(userService.registerWithoutPet(any()))
            .thenReturn(
                UserStatusResponse(
                    nickName = "{랜덤값}",
                    petOwnershipStatus = PetOwnershipStatus.NO_PET,
                    signUpStatus = SignUpStatus.COMPLETE,
                ),
            )

        // when & then
        mockMvc.perform(
            patch("/v1/api/users/pet/none")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(SuccessStatus.OK.code))
            .andExpect(jsonPath("$.result.nickName").value("{랜덤값}"))
            .andExpect(jsonPath("$.result.petOwnershipStatus").value("NO_PET"))
            .andExpect(jsonPath("$.result.signUpStatus").value("COMPLETE"))
    }
}
