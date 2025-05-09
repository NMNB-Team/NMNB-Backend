package nmnb.webflux.common.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import nmnb.webflux.common.properties.S3Properties
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URLEncoder

@Component
class S3Service(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Properties: S3Properties,
) {

    suspend fun upload(fileName: String, filePart: FilePart): String = withContext(Dispatchers.IO) {
        val tempFile = File.createTempFile("upload-", fileName)
        filePart.transferTo(tempFile).awaitSingleOrNull()

        val contentType = filePart.headers().contentType?.toString() ?: "application/octet-stream"

        val request = PutObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(fileName)
            .contentType(contentType)
            .contentLength(tempFile.length())
            .build()

        val requestBody = AsyncRequestBody.fromFile(tempFile.toPath())
        s3AsyncClient.putObject(request, requestBody).await()

        tempFile.delete()

        return@withContext "https://${s3Properties.s3.bucket}.s3.${s3Properties.region.static}.amazonaws.com/${
            URLEncoder.encode(fileName, "UTF-8")
        }"
    }
}
