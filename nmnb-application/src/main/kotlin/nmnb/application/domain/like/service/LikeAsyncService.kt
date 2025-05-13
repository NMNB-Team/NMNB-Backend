package nmnb.application.domain.like.service

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
