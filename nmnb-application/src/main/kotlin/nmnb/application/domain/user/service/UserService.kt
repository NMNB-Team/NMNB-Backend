package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.User

interface UserService {
    fun getProfile(user: User): UserProfileResponse
    fun registerWithPetName(user: User, petName: String): UserStatusResponse
    fun registerWithoutPet(user: User): UserStatusResponse
}
