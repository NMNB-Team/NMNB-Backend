package nmnb.domain.auth

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("refresh_token")
data class RefreshToken(
    @Id
    val userId: String,

    var refreshToken: String,
) {

    fun updateRefreshToken(newToken: String) {
        this.refreshToken = newToken
    }
}
