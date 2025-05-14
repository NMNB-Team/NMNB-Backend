package nmnb.webflux.global.handler.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cloud.aws")
data class S3Properties(
    var s3: S3 = S3(),
    var credentials: Credentials = Credentials(),
    var region: Region = Region(),
) {
    data class S3(
        var bucket: String = "",
    )

    data class Credentials(
        var accessKey: String = "",
        var secretKey: String = "",
    )

    data class Region(
        var static: String = "",
    )
}
