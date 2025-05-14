package nmnb.application.domain.user.service.dto.response

import nmnb.common.utils.DateTimeUtils
import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.User

data class UserProfileResponse(
    val nickName: String,
    val profileImage: String,
    val petOwnershipStatus: PetOwnershipStatus,
    val createdAt: String,
) {
    companion object {
        fun of(user: User): UserProfileResponse {
            return UserProfileResponse(
                nickName = user.nickName,
                profileImage = user.profileImage,
                petOwnershipStatus = user.petOwnershipStatus,
                createdAt = DateTimeUtils.formatDate(user.createdAt),
            )
        }
    }
}
