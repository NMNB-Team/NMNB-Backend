package nmnb.domain.like.repository

interface LikeRepositoryCustom {
    fun delete(userId: String, postId: Long)
}
