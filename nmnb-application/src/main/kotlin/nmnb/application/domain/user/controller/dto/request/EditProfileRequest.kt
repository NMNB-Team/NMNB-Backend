package nmnb.application.domain.user.controller.dto.request

import nmnb.application.domain.user.service.dto.request.EditProfileServiceRequest

data class EditProfileRequest(
    val petName: String? = null,
) {
    fun toServiceRequest(): EditProfileServiceRequest {
        return EditProfileServiceRequest(petName)
    }
}
