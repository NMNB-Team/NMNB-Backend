package nmnb.application.domain.post.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service

@Service
class PostCacheEvictor() {
    @CacheEvict(cacheNames = ["postIds"])
    fun refreshPostIds() {
    }

    @CacheEvict(cacheNames = ["shuffledIds"], key = "#seed")
    fun refreshShuffledIds(seed: Int) {
    }
}
