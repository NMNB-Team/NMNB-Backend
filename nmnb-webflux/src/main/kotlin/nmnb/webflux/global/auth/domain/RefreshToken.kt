package nmnb.webflux.global.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.relational.core.mapping.Table

@Table("refresh_token")
@RedisHash("refresh_token")
data class RefreshToken(
    @Id
    val userId: String,

    var refreshToken: String,
)
