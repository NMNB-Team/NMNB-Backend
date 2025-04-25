package cv.nmnb.domain.user.service.dto.response

import cv.nmnb.domain.user.domain.User
import cv.nmnb.global.utils.DateTimeUtils

data class UserProfileResponse(
    val nickName: String,
    val profileImage: String,
    val hasAnimal: Boolean,
    val createdAt: String,
) {
    companion object {
        fun of(user: User): UserProfileResponse {
            return UserProfileResponse(
                nickName = user.nickName,
                profileImage = user.profileImage,
                hasAnimal = user.hasAnimal,
                createdAt = DateTimeUtils.formatDate(user.createdAt),
            )
        }
    }
}
