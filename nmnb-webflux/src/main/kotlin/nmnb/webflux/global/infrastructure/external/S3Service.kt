package nmnb.webflux.global.infrastructure.external

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import nmnb.common.properties.S3Properties
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URLEncoder
import java.nio.file.Files
import java.util.UUID

@Component
class S3Service(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Properties: S3Properties,
) {
    suspend fun uploadVideo(fileName: String, filePart: FilePart, duration: Int): String = withContext(Dispatchers.IO) {
        val tempFile = createTempFile(filePart)
        try {
            uploadToS3(fileName, tempFile, metadata = mapOf("duration" to duration.toString()))
            return@withContext createPostUrl(fileName)
        } finally {
            tempFile.delete()
        }
    }

    suspend fun uploadThumbnail(fileName: String, file: File): String = withContext(Dispatchers.IO) {
        uploadToS3(fileName, file)
        createPostUrl(fileName)
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
            throw e
        }
    }

    private suspend fun createTempFile(filePart: FilePart): File = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("upload-", ".tmp")
        try {
            filePart.transferTo(tempFile).awaitSingleOrNull()
            tempFile
        } catch (e: Exception) {
            tempFile.delete()
            throw e
        }
    }

    private suspend fun uploadToS3(
        fileName: String,
        file: File,
        metadata: Map<String, String> = emptyMap()
    ) {
        val contentType = withContext(Dispatchers.IO) {
            Files.probeContentType(file.toPath())
        } ?: "application/octet-stream"

        val request = PutObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(fileName)
            .contentType(contentType)
            .contentLength(file.length())
            .metadata(metadata)
            .build()

        val requestBody = AsyncRequestBody.fromFile(file.toPath())
        s3AsyncClient.putObject(request, requestBody).await()
    }

    private fun createPostUrl(fileName: String) =
        "https://${s3Properties.s3.bucket}.s3.${s3Properties.region.static}.amazonaws.com/${
            URLEncoder.encode(fileName, "UTF-8")
        }"
}
