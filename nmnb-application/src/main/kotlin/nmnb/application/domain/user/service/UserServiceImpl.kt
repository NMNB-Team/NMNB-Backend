package nmnb.application.domain.user.service

import nmnb.application.domain.user.exception.PetException
import nmnb.application.domain.user.service.dto.request.EditProfileServiceRequest
import nmnb.application.domain.user.service.dto.request.UserPetRegistrationServiceRequest
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.application.global.infrastructure.external.s3.S3Service
import nmnb.common.domain.PetOwnershipStatus
import nmnb.common.domain.SignUpStatus
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.FileNameUtils.generateFileName
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) : UserService {

    override fun getProfile(user: User): UserProfileResponse {
        return UserProfileResponse.of(user)
    }

    @Transactional
    override fun registerWithPetName(user: User, request: UserPetRegistrationServiceRequest): UserStatusResponse {
        user.updatePetName(request.petName)
        completeRegistration(user, PetOwnershipStatus.HAS_PET)
        return UserStatusResponse.of(userRepository.save(user))
    }

    @Transactional
    override fun registerWithoutPet(user: User): UserStatusResponse {
        completeRegistration(user, PetOwnershipStatus.NO_PET)
        return UserStatusResponse.of(userRepository.save(user))
    }

    private fun completeRegistration(user: User, petOwnershipStatus: PetOwnershipStatus) {
        user.updatePetOwnershipStatus(petOwnershipStatus)
        user.updateSignUpStatus(SignUpStatus.COMPLETE)
    }

    @Transactional
    override fun editProfile(user: User, request: EditProfileServiceRequest, profileImage: MultipartFile?) {
        validatePetName(user.petOwnershipStatus, request.petName)

        val profileImageUrl = profileImage?.let {
            val timeStamp = LocalDateTime.now().toString()
            val originalFileName = it.originalFilename.toString()

            val fileName = generateFileName(timeStamp, originalFileName)
            s3Service.uploadProfileImage(fileName, it)
        }

        user.updateProfile(request.petName, profileImageUrl)
    }

    private fun validatePetName(hasPet: PetOwnershipStatus, petName: String?) {
        when (hasPet) {
            PetOwnershipStatus.HAS_PET -> requireNotNull(petName) {
                throw PetException(ErrorStatus.PET_NAME_REQUIRED)
            }
            PetOwnershipStatus.NO_PET -> require(petName == null) {
                throw PetException(ErrorStatus.PET_REGISTRATION_REQUIRED)
            }
            else -> throw PetException(ErrorStatus.INVALID_PET_STATUS)
        }
    }
}
