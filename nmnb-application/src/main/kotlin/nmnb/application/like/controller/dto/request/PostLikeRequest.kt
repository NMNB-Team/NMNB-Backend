package nmnb.application.like.controller.dto.request

import nmnb.application.like.service.dto.request.PostLikeServiceRequest
import nmnb.domain.user.User

data class PostLikeRequest(
    val user: User,
    val postId: Long,
) {
    fun toServiceRequest(): PostLikeServiceRequest {
        return PostLikeServiceRequest(user, postId)
    }
}
