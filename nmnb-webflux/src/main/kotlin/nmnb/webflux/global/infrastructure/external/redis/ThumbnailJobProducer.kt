package nmnb.webflux.global.infrastructure.external.redis

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class ThumbnailJobProducer(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    suspend fun enqueue(postId: Long, fileName: String) {
        val payload = "$postId|$fileName"
        redisTemplate.opsForList()
            .rightPush("thumbnail:queue", payload)
            .awaitSingle()
    }
}
