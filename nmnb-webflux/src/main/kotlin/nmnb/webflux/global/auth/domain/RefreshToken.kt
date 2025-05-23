package nmnb.webflux.global.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("refresh_token")
data class RefreshToken(
    @Id
    val email: String,

    var refreshToken: String,
)
