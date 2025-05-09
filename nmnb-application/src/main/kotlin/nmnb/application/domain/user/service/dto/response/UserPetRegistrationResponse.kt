package nmnb.application.domain.user.service.dto.response

import nmnb.domain.user.User

data class UserPetRegistrationResponse(
    val petName: String?,
    val hasAnimal: Boolean // 일단은 Boolean으로 했음
) {
    companion object {
        fun of(user: User): UserPetRegistrationResponse {
            return UserPetRegistrationResponse(
                petName = user.petName,
                hasAnimal = user.hasAnimal
            )
        }
    }
}