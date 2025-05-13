package nmnb.application.domain.post.service

import nmnb.application.domain.post.utils.RandomSelector
import nmnb.domain.post.repository.PostRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class PostCacheServiceImpl(
    private val postRepository: PostRepository,
) : PostCacheService {
    @Cacheable(cacheNames = ["postIds"])
    override fun getAllPostIds(): List<Long> = postRepository.findAllPostId()

    @Cacheable(cacheNames = ["shuffledIds"], key = "#seed")
    override fun getShuffledIds(ids: List<Long>, seed: Int): List<Long> {
        return RandomSelector.shuffleIds(ids, seed)
    }
}
