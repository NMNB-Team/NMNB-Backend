package nmnb.application.domain.user.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.user.controller.dto.request.EditProfileRequest
import nmnb.application.domain.user.controller.dto.request.UserPetRegistrationRequest
import nmnb.application.domain.user.exception.PetException
import nmnb.application.global.infrastructure.external.s3.S3Service
import nmnb.common.domain.AccessStrategy
import nmnb.common.domain.PetOwnershipStatus
import nmnb.common.domain.SignUpStatus
import nmnb.common.properties.S3Properties
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional

@Transactional
class UserServiceImplTest : IntegrationTestSupport() {
    @MockBean
    lateinit var s3Service: S3Service

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var s3Properties: S3Properties

    @DisplayName("마이페이지를 조회한다.")
    @Test
    fun getProfile() {
        // given
        val user = User.fixture(
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

    @DisplayName("반려동물이 있는 경우, 마이페이지에서 반려동물 이름을 수정한다.")
    @Test
    fun editProfilePetNameWhenHavePet() {
        // given
        val user = User.fixture(
            petName = "petName",
            petOwnershipStatus = PetOwnershipStatus.HAS_PET,
        )
        val newPetName = "newPetName"
        val reqeust = EditProfileRequest(newPetName, AccessStrategy.PUBLIC_READ)

        // when
        userService.editProfile(user, reqeust.toServiceRequest())

        // then
        assertThat(user.petName).isEqualTo(newPetName)
        assertThat(user.profileImage).isEqualTo("default.png")
    }

    @DisplayName("반려동물이 있는 경우, 반려동물 이름이 null일때 예외를 발생시킨다.")
    @Test
    fun editProfilePetNameWhenHavePetWithPetNameNull() {
        // given
        val user = User.fixture(
            petName = "petName",
            petOwnershipStatus = PetOwnershipStatus.HAS_PET,
        )
        val request = EditProfileRequest(null, AccessStrategy.PUBLIC_READ)

        // when
        val exception = assertThrows<PetException> {
            userService.editProfile(user, request.toServiceRequest())
        }

        assertEquals(ErrorStatus.PET_NAME_REQUIRED, exception.getCode())
    }

    @DisplayName("반려동물이 있는 경우, 마이페이지에서 프로필 사진을 수정한다.")
    @Test
    fun editProfileImageWhenHavePet() {
        // given
        val defaultPetName = "petName"
        val user = User.fixture(
            petName = defaultPetName,
            petOwnershipStatus = PetOwnershipStatus.HAS_PET,
        )
        val profileImage = MockMultipartFile(
            "profileImage",
            "newFileName.png",
            "image/png",
            "image-content".toByteArray(),
        )
        val request = EditProfileRequest(defaultPetName, AccessStrategy.PUBLIC_READ)

        val newImageUrl = "profileImageUrl"
        whenever(s3Service.uploadProfileImage(any(), any(), any())).thenReturn(newImageUrl)

        // when
        userService.editProfile(user, request.toServiceRequest(), profileImage)

        // then
        assertThat(user.petName).isEqualTo("petName")
        assertThat(user.profileImage).isEqualTo(newImageUrl)
    }

    @DisplayName("반려동물이 없는 경우, 마이페이지에서 프로필 이미지를 수정한다.")
    @Test
    fun editProfileImageWhenHaveNoPet() {
        // given
        val user = User.fixture(
            petOwnershipStatus = PetOwnershipStatus.NO_PET,
        )
        val profileImage = MockMultipartFile(
            "profileImage",
            "newFileName.png",
            "image/png",
            "image-content".toByteArray(),
        )
        val request = EditProfileRequest(accessStrategy = AccessStrategy.PUBLIC_READ)

        val newImageUrl = "profileImageUrl"
        whenever(s3Service.uploadProfileImage(any(), any(), any())).thenReturn(newImageUrl)

        // when
        userService.editProfile(user, request.toServiceRequest(), profileImage)

        // then
        assertThat(user.petName).isEqualTo(null)
        assertThat(user.profileImage).isEqualTo(newImageUrl)
    }

    @DisplayName("반려견의 이름을 등록하고 반려견 소유 상태를 설정한다")
    @Test
    fun registerWithPetName() {
        // given
        val user = User.fixture(
            petName = null,
            petOwnershipStatus = PetOwnershipStatus.UNKNOWN,
            signUpStatus = SignUpStatus.IN_PROGRESS,
        )

        val request = UserPetRegistrationRequest(petName = "멍멍이")

        // when
        val result = userService.registerWithPetName(user, request.toServiceRequest())

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
