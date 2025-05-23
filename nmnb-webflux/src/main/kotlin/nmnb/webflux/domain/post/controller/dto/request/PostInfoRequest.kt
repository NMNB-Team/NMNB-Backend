package nmnb.webflux.domain.post.controller.dto.request

import nmnb.webflux.domain.post.service.dto.request.PostInfoServiceRequest

data class PostInfoRequest(
    val description: String? = null,
    val duration: Int,
) {
    fun toServiceRequest(): PostInfoServiceRequest {
        return PostInfoServiceRequest(description, duration)
    }
}
