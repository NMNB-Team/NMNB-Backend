package nmnb.application.like.service.dto.request

import nmnb.domain.user.User

data class PostLikeServiceRequest(
    val user: User,
    val postId: Long,
)
