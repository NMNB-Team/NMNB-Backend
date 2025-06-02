package nmnb.webflux.global.infrastructure.external.redis

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitSingle
import nmnb.webflux.global.infrastructure.external.redis.dto.ThumbnailJobPayload
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class ThumbnailJobProducer(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    suspend fun enqueue(postId: Long, fileName: String) {
        val payload = ThumbnailJobPayload(postId, fileName)
        val json = objectMapper.writeValueAsString(payload)
        redisTemplate.opsForList().rightPush(QUEUE_KEY, json).awaitSingle()
    }

    companion object {
        private const val QUEUE_KEY = "thumbnail:queue"
    }
}
