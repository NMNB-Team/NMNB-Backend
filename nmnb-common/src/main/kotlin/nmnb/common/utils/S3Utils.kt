package nmnb.common.utils

import nmnb.common.properties.S3Properties
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Component
class S3Utils(
    private val s3Properties: S3Properties,
    private val s3Presigner: S3Presigner,
) {

    fun generateS3Key(folder: String, baseFileName: String): String {
        return "$folder/$baseFileName"
    }
    fun generatePresignedUrl(s3Key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(s3Key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(PRESIGNED_URL_EXPIRATION)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    companion object {
        private val PRESIGNED_URL_EXPIRATION: Duration = Duration.ofMinutes(2)
    }
}
