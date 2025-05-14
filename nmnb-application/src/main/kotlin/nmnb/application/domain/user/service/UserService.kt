package nmnb.application.domain.user.service

import nmnb.application.domain.user.service.dto.response.UserPetStatusResponse
import nmnb.application.domain.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.User

interface UserService {
    fun getProfile(user: User): UserProfileResponse
    fun setPetOwnershipWithName(user: User, petName: String): UserPetStatusResponse
    fun setNoPetOwnership(user: User): UserPetStatusResponse
}
