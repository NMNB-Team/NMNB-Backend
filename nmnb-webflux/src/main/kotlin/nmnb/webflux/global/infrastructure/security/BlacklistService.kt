package nmnb.webflux.global.infrastructure.security

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BlacklistService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    fun isBlacklisted(accessToken: String): Mono<Boolean> {
        return redisTemplate.hasKey(getBlacklistKey(accessToken))
    }

    companion object {
        private const val BLACKLIST_KEY_PREFIX = "blacklist:"

        private fun getBlacklistKey(accessToken: String): String {
            return "$BLACKLIST_KEY_PREFIX$accessToken"
        }
    }
}
