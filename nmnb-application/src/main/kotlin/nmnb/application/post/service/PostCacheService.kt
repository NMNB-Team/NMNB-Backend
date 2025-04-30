package nmnb.application.post.service

interface PostCacheService {
    fun getAllPostIds(): List<Long>
    fun getShuffledIds(ids: List<Long>, seed: Int): List<Long>
}
