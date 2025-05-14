package nmnb.application.domain.user.service.dto.response

import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.User

data class UserPetStatusResponse(
    val nickName: String?,
    val petOwnershipStatus: PetOwnershipStatus,
) {
    companion object {
        fun of(user: User): UserPetStatusResponse {
            return UserPetStatusResponse(
                nickName = user.nickName,
                petOwnershipStatus = user.petOwnershipStatus,
            )
        }
    }
}
