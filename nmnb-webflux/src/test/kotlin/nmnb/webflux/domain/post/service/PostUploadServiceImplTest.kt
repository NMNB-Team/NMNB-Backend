package nmnb.webflux.domain.post.service

import kotlinx.coroutines.test.runTest
import nmnb.r2dbc.post.R2dbcPost
import nmnb.r2dbc.post.R2dbcPostRepository
import nmnb.r2dbc.user.R2dbcUser
import nmnb.webflux.IntegrationTestSupport
import nmnb.webflux.domain.post.service.dto.request.PostInfoServiceRequest
import nmnb.webflux.global.infrastructure.external.S3Service
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

class PostUploadServiceImplTest : IntegrationTestSupport() {
    @MockBean
    lateinit var postRepository: R2dbcPostRepository

    @MockBean
    lateinit var s3Service: S3Service

    @Autowired
    lateinit var postUploadService: PostUploadService

    @DisplayName("게시글을 업로드한다.")
    @Test
    fun upload() = runTest {
        // given
        val user = R2dbcUser.fixture()
        val filePart = mock<FilePart> {
            on { filename() } doReturn "test.png"
        }
        val request = PostInfoServiceRequest(description = "test", duration = 10)
        val expectedUrl = "https://s3.aws/test/test.png"
        val savedPost = R2dbcPost.fixture(
            url = expectedUrl,
            description = request.description,
            userId = user.id,
            id = 1L
        )

        whenever(s3Service.uploadVideo(any(), any(), any())).thenReturn(expectedUrl)
        whenever(postRepository.save(any())).thenReturn(Mono.just(savedPost))
        whenever(postRepository.findById(any<Long>())).thenReturn(Mono.just(savedPost))

        // when
        postUploadService.upload(user, filePart, request)

        // then
        verify(postRepository).save(
            argThat {
                url == expectedUrl &&
                    description == request.description &&
                    userId == user.id
            },
        )
    }
}
