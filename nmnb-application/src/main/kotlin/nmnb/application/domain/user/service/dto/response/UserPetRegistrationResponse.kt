package nmnb.application.domain.user.service.dto.response

import nmnb.domain.user.User

data class UserPetRegistrationResponse(
    val petName: String?,
    val hasAnimal: Boolean,
) {
    companion object {
        fun of(user: User): UserPetRegistrationResponse {
            return UserPetRegistrationResponse(
                petName = user.petName,
                hasAnimal = user.hasAnimal,
            )
        }
    }
}
