package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserPetRegistrationResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.User

interface UserService {
    fun getProfile(user: User): UserProfileResponse
    fun registerPet(user: User, petName: String): UserPetRegistrationResponse
}
