package nmnb.domain.like

data class LikeCacheKey(
    private val userId: String,
    private val postId: Long,
) {
    val key: String
        get() = "$LIKE_KEY_PREFIX:$userId:$postId"
    companion object {
        private const val LIKE_KEY_PREFIX = "like"
    }
}
