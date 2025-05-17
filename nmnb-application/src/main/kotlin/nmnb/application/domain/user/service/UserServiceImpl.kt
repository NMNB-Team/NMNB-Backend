package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.common.domain.PetOwnershipStatus
import nmnb.common.domain.SignUpStatus
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    override fun getProfile(user: User): UserProfileResponse {
        return UserProfileResponse.of(user)
    }

    @Transactional
    override fun registerWithPetName(user: User, petName: String): UserStatusResponse {
        user.updatePetName(petName)
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
}
