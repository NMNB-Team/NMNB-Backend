package nmnb.webflux.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "apple")
data class AppleProperties(
    var clientId: String = "",
    var teamId: String = "",
    var keyId: String = "",
    var keyPath: String = "",
    var tokenExpiration: Long = 0L,
    val jwtSetUrl: String = "",
)
