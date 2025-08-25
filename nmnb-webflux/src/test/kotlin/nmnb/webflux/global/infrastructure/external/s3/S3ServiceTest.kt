package nmnb.webflux.global.infrastructure.external.s3

import kotlinx.coroutines.test.runTest
import nmnb.common.auth.repository.RefreshTokenRepository
import nmnb.common.domain.AccessStrategy
import nmnb.webflux.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import java.io.File

class S3ServiceTest(
    @Autowired private val s3Service: S3Service,
) : IntegrationTestSupport() {
    @MockBean
    private lateinit var refreshTokenRepository: RefreshTokenRepository

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
        val result = s3Service.uploadVideo(fileName, filePart, duration, AccessStrategy.PUBLIC_READ)

        // then
        assertThat(result).isNotEmpty
        assertThat(result).startsWith("http").contains(fileName)
    }
}
