package nmnb.application.domain.user.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationRequest
import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.SignUpStatus
import nmnb.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserServiceImplTest(
    @Autowired private val userService: UserService,
) : IntegrationTestSupport() {
    @DisplayName("마이페이지를 조회한다.")
    @Test
    fun getProfile() {
        // given
        val user = User.fixture(
            id = "test",
            petName = "nmnb",
            petOwnershipStatus = PetOwnershipStatus.HAS_PET,
        )

        // when
        val result = userService.getProfile(user)

        // then
        assertThat(result.nickName).isEqualTo(user.nickName)
        assertThat(result.profileImage).isEqualTo(user.profileImage)
        assertThat(result.petOwnershipStatus).isEqualTo(user.petOwnershipStatus)
    }

    @DisplayName("반려견의 이름을 등록하고 반려견 소유 상태를 설정한다")
    @Test
    fun registerWithPetName() {
        // given
        val user = User.fixture(
            id = "test",
            petName = null,
            petOwnershipStatus = PetOwnershipStatus.UNKNOWN,
            signUpStatus = SignUpStatus.IN_PROGRESS,
        )

        val request = UserPetRegistrationRequest(petName = "멍멍이")

        // when
        val result = userService.registerWithPetName(user, request.petName)

        // then
        assertThat(result.nickName).contains("멍멍이")
        assertThat(result.petOwnershipStatus).isEqualTo(PetOwnershipStatus.HAS_PET)
        assertThat(result.signUpStatus).isEqualTo(SignUpStatus.COMPLETE)
    }

    @DisplayName("반려견 미보유 상태로 설정한다")
    @Test
    fun registerWithoutPet() {
        // given
        val user = User.fixture(
            id = "test",
            petName = null,
            petOwnershipStatus = PetOwnershipStatus.UNKNOWN,
            signUpStatus = SignUpStatus.IN_PROGRESS,
        )

        // when
        val result = userService.registerWithoutPet(user)

        // then
        assertThat(result.nickName).isNotBlank()
        assertThat(result.petOwnershipStatus).isEqualTo(PetOwnershipStatus.NO_PET)
        assertThat(result.signUpStatus).isEqualTo(SignUpStatus.COMPLETE)
    }
}
