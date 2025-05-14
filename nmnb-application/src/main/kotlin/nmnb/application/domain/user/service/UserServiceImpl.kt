package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserPetStatusResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.PetOwnershipStatus
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
    override fun setPetOwnershipWithName(user: User, petName: String): UserPetStatusResponse {
        user.updatePetName(petName)
        user.updatePetOwnershipStatus(PetOwnershipStatus.HAS_PET)
        return UserPetStatusResponse.of(userRepository.save(user))
    }

    @Transactional
    override fun setNoPetOwnership(user: User): UserPetStatusResponse {
        user.updatePetOwnershipStatus(PetOwnershipStatus.NO_PET)
        return UserPetStatusResponse.of(userRepository.save(user))
    }
}
