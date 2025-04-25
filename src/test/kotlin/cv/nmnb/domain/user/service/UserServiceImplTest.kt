package cv.nmnb.domain.user.service

import cv.nmnb.IntegrationTestSupport
import cv.nmnb.domain.user.domain.PetOwnershipStatus.HAS_PET
import cv.nmnb.domain.user.domain.User
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
            companionAnimal = "nmnb",
            petOwnershipStatus = HAS_PET,
        )

        // when
        val result = userService.getProfile(user)

        // then
        assertThat(result.nickName).isEqualTo(user.nickName)
        assertThat(result.profileImage).isEqualTo(user.profileImage)
        assertThat(result.hasAnimal).isEqualTo(user.hasAnimal)
    }
}
