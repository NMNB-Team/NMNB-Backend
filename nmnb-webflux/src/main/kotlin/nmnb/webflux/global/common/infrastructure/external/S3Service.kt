package nmnb.webflux.global.common.infrastructure.external

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import nmnb.common.properties.S3Properties
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URLEncoder
import java.nio.file.Files

@Component
class S3Service(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Properties: S3Properties,
) {

    suspend fun upload(fileName: String, filePart: FilePart, duration: Int): String = withContext(Dispatchers.IO) {
        val tempFile = createTempFile(fileName, filePart)
        try {
            uploadToS3(fileName, tempFile, duration)
            return@withContext createPostUrl(fileName)
        } finally {
            tempFile.delete()
        }
    }

    private suspend fun createTempFile(fileName: String, filePart: FilePart): File {
        val tempFile = File.createTempFile("upload-", fileName)
        filePart.transferTo(tempFile).awaitSingleOrNull()
        return tempFile
    }

    private suspend fun uploadToS3(fileName: String, file: File, duration: Int) {
        val contentType = withContext(Dispatchers.IO) {
            Files.probeContentType(file.toPath())
        } ?: "application/octet-stream"
        val metadata = mapOf("duration" to duration.toString())

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
