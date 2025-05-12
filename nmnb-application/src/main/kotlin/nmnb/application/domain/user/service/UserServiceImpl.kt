package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserPetRegistrationResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserServiceImp(
    private val userRepository: UserRepository
) : UserService {

    override fun getProfile(user: User): UserProfileResponse {
        return UserProfileResponse.of(user)
    }

    @Transactional(readOnly = false)
    override fun registerPet(user: User, petName: String): UserPetRegistrationResponse {
        user.updatePetName(petName)
        user.updatePetOwnershipStatus(PetOwnershipStatus.HAS_PET)
        return UserPetRegistrationResponse.of(userRepository.save(user))
    }
}
