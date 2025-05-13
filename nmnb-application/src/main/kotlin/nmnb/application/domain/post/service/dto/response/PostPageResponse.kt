package nmnb.application.domain.post.service.dto.response

import nmnb.domain.post.Post
import nmnb.domain.user.User

data class PostPageResponse(
    val postInfo: List<PostInfoResponse>,
    val hasNext: Boolean,
    val nextCursor: Int?,
) {
    companion object {
        fun of(postInfo: List<PostInfoResponse>, hasNext: Boolean, nextCursor: Int?): PostPageResponse {
            return PostPageResponse(
                postInfo = postInfo,
                hasNext = hasNext,
                nextCursor = nextCursor,
            )
        }
    }
}

data class PostInfoResponse(
    val postId: Long?,
    val url: String,
    val thumbnailUrl: String,
    val description: String?,
    val userInfo: UserInfoResponse,
) {
    companion object {
        fun of(post: Post): PostInfoResponse {
            return PostInfoResponse(
                postId = post.id,
                url = post.url,
                thumbnailUrl = post.thumbnailUrl,
                description = post.description,
                userInfo = UserInfoResponse.of(post.user),
            )
        }
    }
}

data class UserInfoResponse(
    val nickName: String,
    val profileImage: String,
) {
    companion object {
        fun of(user: User): UserInfoResponse {
            return UserInfoResponse(
                nickName = user.nickName,
                profileImage = user.profileImage,
            )
        }
    }
}
