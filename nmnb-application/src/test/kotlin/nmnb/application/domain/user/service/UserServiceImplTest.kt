package nmnb.application.domain.user.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationRequest
import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.User
import org.assertj.core.api.Assertions
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
        Assertions.assertThat(result.nickName).isEqualTo(user.nickName)
        Assertions.assertThat(result.profileImage).isEqualTo(user.profileImage)
        Assertions.assertThat(result.hasAnimal).isEqualTo(user.hasAnimal)
    }

    @DisplayName("반려견을 등록한다")
    @Test
    fun registerPet() {
        // given
        val user = User.fixture(
            id = "test",
            companionAnimal = null,
            petOwnershipStatus = PetOwnershipStatus.NO_PET,
        )

        val request = UserPetRegistrationRequest(petName = "멍멍이")

        // when
        val result = userService.registerPet(user, request.petName)

        // then
        Assertions.assertThat(result.petName).isEqualTo("멍멍이")
        Assertions.assertThat(result.hasAnimal).isEqualTo(true)
    }
}
