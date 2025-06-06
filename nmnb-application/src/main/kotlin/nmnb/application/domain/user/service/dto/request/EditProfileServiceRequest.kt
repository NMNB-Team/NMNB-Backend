package nmnb.application.domain.user.service.dto.request

import nmnb.common.domain.AccessStrategy

data class EditProfileServiceRequest(
    val petName: String?,
    val accessStrategy: AccessStrategy,
)
