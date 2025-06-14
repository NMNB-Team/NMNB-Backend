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
    fun update(refreshToken: String, timeStamp: LocalDateTime) {
        this.refreshToken = refreshToken
        this.timeStamp = timeStamp
    }

    companion object {
        fun fixture(
            email: String = "email@gmail.com",
            refreshToken: String,
            timeStamp: LocalDateTime,
            deviceId: String,
        ): RefreshToken {
            return RefreshToken(
                id = "$email:$deviceId",
                email = email,
                refreshToken = refreshToken,
                timeStamp = timeStamp,
                deviceId = deviceId,
            )
        }
    }
}
