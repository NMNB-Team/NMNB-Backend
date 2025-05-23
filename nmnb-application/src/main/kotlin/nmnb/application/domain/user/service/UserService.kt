package nmnb.application.domain.user.service

import nmnb.application.domain.user.controller.dto.request.UserPetRegistrationRequest
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.application.domain.user.service.dto.response.UserStatusResponse
import nmnb.domain.user.User

interface UserService {
    fun getProfile(user: User): UserProfileResponse
    fun registerWithPetName(user: User, request: UserPetRegistrationRequest): UserStatusResponse
    fun registerWithoutPet(user: User): UserStatusResponse
}
