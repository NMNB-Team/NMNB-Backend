package cv.nmnb.domain.user.service.dto.response

import cv.nmnb.domain.user.domain.User
import java.time.LocalDateTime

data class UserProfileResponse(
    val nickName: String,
    val profileImage: String,
    val hasAnimal: Boolean,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun of(user: User): UserProfileResponse {
            return UserProfileResponse(
                nickName = user.nickName,
                profileImage = user.profileImage,
                hasAnimal = user.hasAnimal,
                createdAt = user.createdAt,
            )
        }
    }
}
