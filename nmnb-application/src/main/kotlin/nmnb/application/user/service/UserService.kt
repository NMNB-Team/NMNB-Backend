package nmnb.application.user.service

import nmnb.application.user.service.dto.response.UserProfileResponse
import nmnb.domain.user.User

interface UserService {
    fun getProfile(user: User): UserProfileResponse
}
