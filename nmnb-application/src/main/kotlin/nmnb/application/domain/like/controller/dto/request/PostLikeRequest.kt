package nmnb.application.domain.like.controller.dto.request

import nmnb.application.domain.like.service.dto.request.PostLikeServiceRequest

data class PostLikeRequest(
    val postId: Long,
) {
    fun toServiceRequest(): PostLikeServiceRequest {
        return PostLikeServiceRequest(postId)
    }
}
