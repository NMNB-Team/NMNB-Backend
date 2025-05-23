package nmnb.application.domain.like.service

import nmnb.application.domain.like.service.dto.request.PostLikeServiceRequest
import nmnb.domain.like.LikeCacheKey
import nmnb.domain.user.User
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class LikeServiceImpl(
    private val likeAsyncService: LikeAsyncService,
    private val redisTemplate: StringRedisTemplate,
) : LikeService {

    @Transactional
    override fun likeOrUnlike(user: User, request: PostLikeServiceRequest) {
        val likeCacheKey = LikeCacheKey(user.id!!, request.postId)

        if (redisTemplate.hasKey(likeCacheKey.key)) {
            deleteLikeCache(likeCacheKey)
            likeAsyncService.deleteLikeToDB(user, request.postId)
        } else {
            setLikeCache(likeCacheKey)
            likeAsyncService.saveLikeToDB(user, request.postId)
        }
    }

    private fun setLikeCache(likeCacheKey: LikeCacheKey) {
        redisTemplate.opsForValue().set(likeCacheKey.key, "1")
    }

    private fun deleteLikeCache(likeCacheKey: LikeCacheKey) {
        redisTemplate.delete(likeCacheKey.key)
    }
}
