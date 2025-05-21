package nmnb.application.global.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cloud.aws.s3")
class S3Properties {

    lateinit var bucket: String
    lateinit var urlPrefix: String
    lateinit var defaultProfileImagePath: String

    val defaultProfileImageUrl: String
        get() = "$urlPrefix/$defaultProfileImagePath"
}
