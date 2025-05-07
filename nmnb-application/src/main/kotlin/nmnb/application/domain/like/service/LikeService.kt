package nmnb.application.domain.like.service

import nmnb.application.domain.like.service.dto.request.PostLikeServiceRequest
import nmnb.domain.user.User

interface LikeService {
    fun likeOrUnlike(user: User, request: PostLikeServiceRequest)
}
