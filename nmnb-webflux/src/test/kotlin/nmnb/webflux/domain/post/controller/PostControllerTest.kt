package nmnb.webflux.domain.post.controller

import kotlinx.coroutines.runBlocking
import nmnb.common.response.status.SuccessStatus
import nmnb.common.utils.JwtConstants.DEVICE_ID_CLAIM_KEY
import nmnb.r2dbc.user.R2dbcUser
import nmnb.webflux.ControllerTestSupport
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono

class PostControllerTest : ControllerTestSupport() {
    @BeforeEach
    fun setup() {
        val user = R2dbcUser(
            email = "test@example.com",
            profileImage = "profile",
        )
        given(userRepository.findByEmail(any())).willReturn(Mono.just(user))
    }

    @DisplayName("게시글을 업로드하는데 성공한다.")
    @Test
    fun uploadPost() = runBlocking {
        // given
        val builder = MultipartBodyBuilder()
        builder.part("file", ByteArrayResource("test content".toByteArray()))
            .filename("test.txt")
            .contentType(MediaType.TEXT_PLAIN)
        builder.part("request", """{"description":"description", "duration":"10",  "accessStrategy": "PUBLIC_READ"}""")

        val multipartData = builder.build()
        val accessToken = "access-token"
        val deviceId = "deviceId"
        val user = R2dbcUser.fixture()
        mockUserAuthentication(accessToken, user, deviceId)

        // when & then
        webTestClient.post()
            .uri("/netty/v1/api/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipartData))
            .header("X-Access-Token", accessToken)
            .header("Device-Id", deviceId)
            .exchange()
            .expectBody()
            .jsonPath("$.code").isEqualTo(SuccessStatus.NO_CONTENT.code)
            .jsonPath("$.message").isEqualTo(SuccessStatus.NO_CONTENT.message)

        verify(postUploadService, times(1)).upload(any(), any(), any())
    }

    private fun mockUserAuthentication(accessToken: String, user: R2dbcUser, deviceId: String) {
        whenever(jwtProvider.isValidToken(accessToken)).thenReturn(true)
        whenever(jwtProvider.getEmail(accessToken)).thenReturn(user.email)
        whenever(jwtProvider.getClaimFromToken(accessToken, DEVICE_ID_CLAIM_KEY)).thenReturn(
            deviceId,
        )
        whenever(blacklistService.isBlacklisted(accessToken)).thenReturn(Mono.just(false))
        whenever(userRepository.findByEmail(user.email)).thenReturn(Mono.just(user))
    }
}
