package nmnb.application.like.service

import nmnb.domain.user.User

interface LikeAsyncService {
    fun saveLikeToDB(
        user: User,
        postId: Long,
    )
    fun deleteLikeToDB(
        user: User,
        postId: Long,
    )
}
