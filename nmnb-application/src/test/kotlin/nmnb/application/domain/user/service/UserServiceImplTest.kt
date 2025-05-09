package nmnb.application.domain.user.service

import nmnb.application.IntegrationTestSupport
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
}
