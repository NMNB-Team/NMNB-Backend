package nmnb.application.post.service

import nmnb.domain.post.repository.PostRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class PostCacheServiceImpl(
    private val postRepository: PostRepository,
) : PostCacheService {
    @Cacheable(cacheNames = ["postIds"])
    override fun getAllPostIds(): List<Long> = postRepository.findAllPostId()
}
