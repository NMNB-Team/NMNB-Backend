package nmnb.application.global.infrastructure.security

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Transactional(readOnly = true)
@Service
class BlacklistService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val jwtProvider: JwtProvider,
) {
    @Transactional
    fun register(accessToken: String) {
        val ttl = jwtProvider.getRemainingTtl(accessToken)

        if (ttl > 0) {
            redisTemplate.opsForValue()
                .set(getBlacklistKey(accessToken), BLACKLIST_VALUE, ttl, TimeUnit.MILLISECONDS)
        }
    }

    companion object {
        private const val BLACKLIST_KEY_PREFIX = "blacklist:"
        private const val BLACKLIST_VALUE = "logout"

        fun getBlacklistKey(accessToken: String): String {
            return "$BLACKLIST_KEY_PREFIX$accessToken"
        }
    }
}
