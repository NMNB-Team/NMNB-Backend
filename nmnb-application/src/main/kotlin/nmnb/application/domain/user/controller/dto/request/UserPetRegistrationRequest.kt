package nmnb.application.domain.user.controller.dto.request

data class UserPetRegistrationRequest(
    val petName: String,
) {
    fun toServiceRequest(): UserPetRegistrationRequest {
        return UserPetRegistrationRequest(petName)
    }
}
