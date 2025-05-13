package nmnb.application.domain.like.controller.dto.request

import nmnb.application.domain.like.service.dto.request.PostLikeServiceRequest
import nmnb.domain.user.User

data class PostLikeRequest(
    val user: User,
    val postId: Long,
) {
    fun toServiceRequest(): PostLikeServiceRequest {
        return PostLikeServiceRequest(user, postId)
    }
}
