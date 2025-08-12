package nmnb.application.domain.post.service

interface PostCacheService {
    fun getAllPostIds(): List<Long>
    fun getShuffledIds(ids: List<Long>, seed: Int): List<Long>
    fun refreshPostcache(seed: Int)
}
