package nmnb.webflux.global.infrastructure.external

import kotlinx.coroutines.test.runTest
import nmnb.webflux.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File

class FfmpegServiceTest(
    @Autowired private val ffmpegService: FfmpegService,
) : IntegrationTestSupport() {

    @Test
    @DisplayName("FFmpeg를 이용하여 썸네일 이미지를 성공적으로 생성한다")
    fun createThumbnail() = runTest {
        // given
        val resource = javaClass.getResource("/sample-video.mp4")!!
        val videoFile = File(resource.toURI())

        // when
        val resultThumbnail = ffmpegService.createThumbnail(videoFile)

        // then
        assertThat(resultThumbnail).exists()
        assertThat(resultThumbnail.length()).isGreaterThan(0)
        assertThat(resultThumbnail.extension).isEqualTo("jpg")
    }
}
