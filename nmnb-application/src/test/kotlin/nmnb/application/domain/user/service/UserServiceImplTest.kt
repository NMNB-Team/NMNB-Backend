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
        Assertions.assertThat(result.petOwnershipStatus).isEqualTo(user.petOwnershipStatus)
    }

    @DisplayName("반려견의 이름을 등록하고 반려견 소유 상태를 설정한다")
    @Test
    fun setPetOwnershipWithName() {
        // given
        val user = User.fixture(
            id = "test",
            petName = null,
            petOwnershipStatus = PetOwnershipStatus.UNKNOWN,
        )

        val request = UserPetRegistrationRequest(petName = "멍멍이")

        // when
        val result = userService.setPetOwnershipWithName(user, request.petName)

        // then
        Assertions.assertThat(result.nickName).contains("멍멍이")
        Assertions.assertThat(result.petOwnershipStatus).isEqualTo(PetOwnershipStatus.HAS_PET)
    }

    @DisplayName("반려견 미보유 상태로 설정한다")
    @Test
    fun setNoPetOwnership() {
        // given
        val user = User.fixture(
            id = "test",
            petName = null,
            petOwnershipStatus = PetOwnershipStatus.UNKNOWN,
        )

        // when
        val result = userService.setNoPetOwnership(user)

        // then
        Assertions.assertThat(result.nickName).isNotBlank()
        Assertions.assertThat(result.petOwnershipStatus).isEqualTo(PetOwnershipStatus.NO_PET)
    }
}
