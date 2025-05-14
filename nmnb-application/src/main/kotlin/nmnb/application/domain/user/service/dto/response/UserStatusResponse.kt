package nmnb.application.domain.user.service.dto.response

import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.SignUpStatus
import nmnb.domain.user.User

data class UserStatusResponse(
    val nickName: String,
    val petOwnershipStatus: PetOwnershipStatus,
    val signUpStatus: SignUpStatus,
) {
    companion object {
        fun of(user: User): UserStatusResponse {
            return UserStatusResponse(
                nickName = user.nickName,
                petOwnershipStatus = user.petOwnershipStatus,
                signUpStatus = user.signUpStatus
            )
        }
    }
}
