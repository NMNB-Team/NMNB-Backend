package nmnb.webflux.global.infrastructure.external.s3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import nmnb.common.properties.S3Properties
import nmnb.common.response.exception.PostException
import nmnb.common.response.status.ErrorStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.File
import java.nio.file.Files
import java.time.Duration
import java.util.UUID

@Component
class S3Service(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Presigner: S3Presigner,
    private val s3Properties: S3Properties,
) {

    suspend fun uploadVideo(baseFileName: String, filePart: FilePart, duration: Int): String = withContext(Dispatchers.IO) {
        val s3Key = generateS3Key(VIDEO_FOLDER, baseFileName)
        val tempFile = createTempFile(filePart)
        try {
            uploadToS3(s3Key, tempFile, metadata = mapOf("duration" to duration.toString()))
            return@withContext generatePresignedUrl(s3Key)
        } finally {
            tempFile.delete()
        }
    }

    suspend fun uploadThumbnail(baseFileName: String, file: File): String = withContext(Dispatchers.IO) {
        val s3Key = generateS3Key(THUMBNAIL_FOLDER, baseFileName)
        uploadToS3(s3Key, file)
        generatePresignedUrl(s3Key)
    }

    suspend fun download(fileName: String): File = withContext(Dispatchers.IO) {
        val tempFile = File(System.getProperty("java.io.tmpdir"), "video-${UUID.randomUUID()}.tmp")
        try {
            val request = GetObjectRequest.builder()
                .bucket(s3Properties.s3.bucket)
                .key(fileName)
                .build()

            s3AsyncClient.getObject(request, AsyncResponseTransformer.toFile(tempFile.toPath())).await()
            tempFile
        } catch (e: Exception) {
            tempFile.delete()
            throw PostException(ErrorStatus.S3_DOWNLOAD_POST_FAILED)
        }
    }

    private suspend fun createTempFile(filePart: FilePart): File = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("upload-", ".tmp")
        try {
            filePart.transferTo(tempFile).awaitSingleOrNull()
            tempFile
        } catch (e: Exception) {
            tempFile.delete()
            throw PostException(ErrorStatus.POST_THUMBNAIL_GENERATION_FAILED)
        }
    }

    private suspend fun uploadToS3(
        s3Key: String,
        file: File,
        metadata: Map<String, String> = emptyMap(),
    ) {
        val contentType = withContext(Dispatchers.IO) {
            Files.probeContentType(file.toPath())
        } ?: "application/octet-stream"

        val request = PutObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(s3Key)
            .contentType(contentType)
            .contentLength(file.length())
            .metadata(metadata)
            .build()

        val requestBody = AsyncRequestBody.fromFile(file.toPath())
        s3AsyncClient.putObject(request, requestBody).await()
    }

    private fun generatePresignedUrl(key: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .getObjectRequest(getObjectRequest)
            .signatureDuration(Duration.ofHours(1))
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }

    private fun generateS3Key(folder: String, baseFileName: String): String {
        return "$folder/$baseFileName"
    }

    companion object {
        private const val VIDEO_FOLDER = "video"
        private const val THUMBNAIL_FOLDER = "thumbnail"
    }
}
