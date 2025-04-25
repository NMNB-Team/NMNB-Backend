package cv.nmnb.domain.user.service

import cv.nmnb.domain.user.domain.User
import cv.nmnb.domain.user.service.dto.response.UserProfileResponse

interface UserService {
    fun getProfile(user: User): UserProfileResponse
}
