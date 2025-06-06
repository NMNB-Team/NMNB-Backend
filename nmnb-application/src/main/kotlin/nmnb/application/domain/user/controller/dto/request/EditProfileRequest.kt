package nmnb.application.domain.user.controller.dto.request

import nmnb.application.domain.user.service.dto.request.EditProfileServiceRequest
import nmnb.common.domain.AccessStrategy

data class EditProfileRequest(
    val petName: String? = null,
    val accessStrategy: AccessStrategy,
) {
    fun toServiceRequest(): EditProfileServiceRequest {
        return EditProfileServiceRequest(petName, accessStrategy)
    }
}
