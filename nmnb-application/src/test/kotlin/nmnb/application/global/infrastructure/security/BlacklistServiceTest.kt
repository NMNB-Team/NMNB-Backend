package nmnb.application.global.infrastructure.security

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class BlacklistServiceTest {
    @InjectMocks
    lateinit var blacklistService: BlacklistService

    @Mock
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    lateinit var jwtProvider: JwtProvider

    @Mock
    lateinit var valueOperations: ValueOperations<String, String>

    @DisplayName("유효한 TTL이 있는 AccessToken을 Redis 블랙리스트에 정상 저장한다")
    @Test
    fun register() {
        // given
        val accessToken = "token"
        val ttl = 100L
        val key = "blacklist:$accessToken"

        whenever(jwtProvider.getRemainingTtl(accessToken)).thenReturn(ttl)
        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        // when
        blacklistService.register(accessToken)

        // then
        verify(valueOperations).setIfAbsent(eq(key), eq("logout"), eq(ttl), eq(TimeUnit.MILLISECONDS))
    }

    @DisplayName("유효하지 않은 TTL이 있는 AccessToken를 저장하려고할때 예외가 발생한다.")
    @Test
    fun registerWhenTTLIsInvalid() {
        // given
        val accessToken = "token"

        whenever(jwtProvider.getRemainingTtl(accessToken)).thenReturn(0L)

        // when
        blacklistService.register(accessToken)

        // then
        verify(valueOperations, never()).set(any(), any(), any(), any())
    }
}
