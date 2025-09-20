package nmnb.application.domain.post.service

import nmnb.application.domain.post.service.dto.request.MyPostPageServiceRequest
import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest
import nmnb.application.domain.post.service.dto.response.MyPostPageResponse
import nmnb.application.domain.post.service.dto.response.PostPageResponse
import nmnb.domain.user.User

interface PostService {
    fun getPostPage(userId: String?, request: PostPageServiceRequest): PostPageResponse
    fun deletePost(user: User, postId: Long)
    fun getMyPost(user: User, request: MyPostPageServiceRequest): MyPostPageResponse
}
