package nmnb.application.domain.user.service.dto.response

import nmnb.domain.user.PetOwnershipStatus
import nmnb.domain.user.User

data class UserPetRegistrationResponse(
    val petName: String?,
    val petOwnershipStatus: PetOwnershipStatus,
) {
    companion object {
        fun of(user: User): UserPetRegistrationResponse {
            return UserPetRegistrationResponse(
                petName = user.petName,
                petOwnershipStatus = user.petOwnershipStatus,
            )
        }
    }
}
