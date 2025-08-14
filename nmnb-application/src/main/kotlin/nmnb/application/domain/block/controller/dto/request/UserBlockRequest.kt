package nmnb.application.domain.block.controller.dto.request

import nmnb.application.domain.block.service.dto.request.UserBlockServiceRequest

data class UserBlockRequest(
    val userId: String,
) {
    fun toUserBlockServiceRequest(): UserBlockServiceRequest {
        return UserBlockServiceRequest(userId)
    }
}
