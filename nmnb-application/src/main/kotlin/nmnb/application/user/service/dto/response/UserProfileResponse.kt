package nmnb.application.user.service.dto.response

import nmnb.common.utils.DateTimeUtils
import nmnb.domain.user.User

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
