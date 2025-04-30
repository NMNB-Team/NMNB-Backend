package nmnb.application.like.service

import nmnb.application.like.service.dto.request.PostLikeServiceRequest
import nmnb.domain.user.User

interface LikeService {
    fun likeOrUnlike(user: User, request: PostLikeServiceRequest)
}
