package nmnb.application.domain.post.service

import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest
import nmnb.application.domain.post.utils.RandomSelector
import nmnb.domain.post.repository.PostRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class PostCacheServiceImpl(
    private val postRepository: PostRepository,
    private val postCacheEvictor: PostCacheEvictor,
) : PostCacheService {
    @Cacheable(cacheNames = ["postIds"])
    override fun getAllPostIds(): List<Long> = postRepository.findAllPostId()

    @Cacheable(cacheNames = ["shuffledIds"], key = "#seed")
    override fun getShuffledIds(ids: List<Long>, seed: Int): List<Long> {
        return RandomSelector.shuffleIds(ids, seed)
    }

    override fun refreshPostcache(request: PostPageServiceRequest) {
        if (request.cursor == INITIAL_CURSOR) {
            postCacheEvictor.refreshPostIds()
            postCacheEvictor.refreshShuffledIds(request.seed)
        }
    }

    companion object {
        private const val INITIAL_CURSOR = -1
    }
}
