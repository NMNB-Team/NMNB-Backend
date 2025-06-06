package nmnb.application.global.infrastructure.external.s3

import nmnb.application.IntegrationTestSupport
import nmnb.common.domain.AccessStrategy
import nmnb.common.response.exception.S3Exception
import nmnb.common.response.status.ErrorStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream

class S3ServiceTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var s3Service: S3Service

    @MockBean
    private lateinit var s3Client: S3Client

    @Mock
    private lateinit var mockMultipartFile: MultipartFile

    @DisplayName("프로필 사진 업로드에 성공한다.")
    @Test
    fun uploadProfileImage() {
        val fileName = "test-profile.jpg"
        val fileContent = "content".toByteArray()

        whenever(mockMultipartFile.inputStream).thenReturn(ByteArrayInputStream(fileContent))
        whenever(mockMultipartFile.size).thenReturn(fileContent.size.toLong())
        whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>())).thenReturn(mock())

        // When
        val result = s3Service.uploadProfileImage(fileName, mockMultipartFile, AccessStrategy.PUBLIC_READ)

        assertThat(result).isNotEmpty
        assertThat(result).startsWith("http").contains(fileName)

        verify(s3Client, times(1)).putObject(any<PutObjectRequest>(), any<RequestBody>())
        verify(mockMultipartFile, times(1)).inputStream
    }

    @DisplayName("S3에 프로필 사진 업로드 실패시 예외가 발생한다.")
    @Test
    fun uploadProfileImageThrowsExceptionWhenS3Fails() {
        // Given
        val fileName = "test-profile.jpg"
        val fileContent = "content".toByteArray()

        whenever(mockMultipartFile.inputStream).thenReturn(ByteArrayInputStream(fileContent))
        whenever(mockMultipartFile.size).thenReturn(fileContent.size.toLong())
        whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
            .thenThrow(SdkClientException.builder().message("S3 connection failed").build())

        // When & Then
        val exception = assertThrows<S3Exception> {
            s3Service.uploadProfileImage(fileName, mockMultipartFile, AccessStrategy.PUBLIC_READ)
        }

        assertEquals(ErrorStatus.S3_UPLOAD_PROFILE_IMAGE_FAILED, exception.getCode())
        verify(s3Client, times(1)).putObject(any<PutObjectRequest>(), any<RequestBody>())
    }
}
