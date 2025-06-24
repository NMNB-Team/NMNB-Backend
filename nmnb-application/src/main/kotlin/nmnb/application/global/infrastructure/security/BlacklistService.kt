package nmnb.application.global.infrastructure.security

import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(BlacklistService::class.java)

    @Transactional
    fun register(accessToken: String) {
        val ttl = jwtProvider.getRemainingTtl(accessToken)

        if (ttl <= 0) return

        val success =
            setBlacklistValueIfNotExists(getBlacklistKey(accessToken), BLACKLIST_VALUE, ttl, TimeUnit.MILLISECONDS)
        if (!success) {
            logger.warn("이미 등록된 accessToken 블랙리스트 요청: {}", accessToken)
        }
    }

    fun setBlacklistValueIfNotExists(key: String, value: String, timeout: Long, unit: TimeUnit): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit) ?: false
    }

    fun isBlacklisted(accessToken: String): Boolean {
        return redisTemplate.hasKey(getBlacklistKey(accessToken))
    }

    companion object {
        private const val BLACKLIST_KEY_PREFIX = "blacklist:"
        private const val BLACKLIST_VALUE = "logout"

        private fun getBlacklistKey(accessToken: String): String {
            return "$BLACKLIST_KEY_PREFIX$accessToken"
        }
    }
}
