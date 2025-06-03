package nmnb.application.global.infrastructure.external.s3

import nmnb.common.properties.S3Properties
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Service
class S3Service(
    private val s3Presigner: S3Presigner,
    private val s3Properties: S3Properties,
) {
    private fun generatePresignedUrl(key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(PRESIGNED_URL_EXPIRATION)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    companion object {
        private val PRESIGNED_URL_EXPIRATION: Duration = Duration.ofHours(1)
    }
}
