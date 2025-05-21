package nmnb.webflux.global.config

import nmnb.webflux.global.common.properties.S3Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

@Configuration
class S3Config(
    private val s3Properties: S3Properties,
) {
    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        val credentials = AwsBasicCredentials.create(s3Properties.credentials.accessKey, s3Properties.credentials.secretKey)
        return S3AsyncClient.builder()
            .region(Region.of(s3Properties.region.static))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
}
