package nmnb.application.global.infrastructure.external.s3

import nmnb.common.properties.S3Properties
import nmnb.common.response.exception.S3Exception
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.S3Utils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties,
    private val s3Utils: S3Utils,
) {
    fun uploadProfileImage(fileName: String, profileImage: MultipartFile): String {
        val s3Key = s3Utils.generateS3Key(PROFILE_IMAGE_PATH, fileName)
        val request = createRequest(s3Key, profileImage)

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(profileImage.inputStream, profileImage.size))
        } catch (e: SdkClientException) {
            throw S3Exception(ErrorStatus.S3_UPLOAD_PROFILE_IMAGE_FAILED)
        }

        return s3Utils.generatePresignedUrl(s3Key)
    }

    private fun createRequest(s3Key: String, profileImage: MultipartFile): PutObjectRequest =
        PutObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(s3Key)
            .contentType(profileImage.contentType)
            .contentLength(profileImage.size)
            .build()

    companion object {
        private const val PROFILE_IMAGE_PATH = "profile"
    }
}
