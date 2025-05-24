package nmnb.webflux.global.common.infrastructure

import kotlinx.coroutines.test.runTest
import nmnb.common.properties.S3Properties
import nmnb.webflux.IntegrationTestSupport
import nmnb.webflux.global.common.infrastructure.s3.S3Service
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import java.io.File
import java.net.URLEncoder

class S3ServiceTest(
    @Autowired private val s3Properties: S3Properties,
    @Autowired private val s3Service: S3Service,
) : IntegrationTestSupport() {
    @DisplayName("게시글을 S3에 업로드한 후, Url을 응답하는데 성공한다")
    @Test
    fun upload() = runTest {
        // given
        val fileName = "tmp"
        val filePart = mock(FilePart::class.java)
        val headers = mock(HttpHeaders::class.java)

        whenever(headers.contentType).thenReturn(MediaType.parseMediaType("video/mp4"))
        whenever(filePart.headers()).thenReturn(headers)
        whenever(filePart.transferTo(any(File::class.java))).thenReturn(Mono.empty())

        val duration = 10

        // when
        val result = s3Service.upload(fileName, filePart, duration)

        // then
        val expectFileName = "https://${s3Properties.s3.bucket}.s3.${s3Properties.region.static}.amazonaws.com/${
            URLEncoder.encode(fileName, "UTF-8")
        }"

        assertThat(result).isEqualTo(expectFileName)
    }
}
