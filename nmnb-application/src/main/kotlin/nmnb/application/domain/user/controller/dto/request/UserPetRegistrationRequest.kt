package nmnb.application.domain.user.controller.dto.request

import nmnb.application.domain.user.service.dto.request.UserPetRegistrationServiceRequest

data class UserPetRegistrationRequest(
    val petName: String,
) {
    fun toServiceRequest(): UserPetRegistrationServiceRequest {
        return UserPetRegistrationServiceRequest(petName)
    }
}
