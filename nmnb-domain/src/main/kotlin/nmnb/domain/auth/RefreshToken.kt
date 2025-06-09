package nmnb.domain.auth

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime

@RedisHash("refresh_token")
data class RefreshToken(
    @Id
    val id: String,
    var email: String,
    var refreshToken: String,
    var deviceId: String,
    var timeStamp: LocalDateTime,

    ) {

    fun update(newToken: String) {
        this.refreshToken = newToken
    }
}
