package nmnb.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "",
    var refreshExpirationTime: Long = 0L,
    var accessExpirationTime: Long = 0L,
)
