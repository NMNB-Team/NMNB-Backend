package nmnb.webflux.domain.post.service

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import kotlinx.coroutines.reactive.awaitSingle
import nmnb.r2dbc.post.R2dbcPost
import nmnb.r2dbc.post.R2dbcPostRepository
import nmnb.r2dbc.user.R2dbcUser
import nmnb.webflux.domain.post.service.dto.request.PostInfoServiceRequest
import nmnb.webflux.global.common.infrastructure.external.S3Service
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PostUploadServiceImpl(
    private val postRepository: R2dbcPostRepository,
    private val s3Service: S3Service,
) : PostUploadService {

    @Transactional
    override suspend fun upload(user: R2dbcUser, file: FilePart, request: PostInfoServiceRequest) {
        val fileName = generateFileName(LocalDate.now().toString(), file.filename())

        val url = s3Service.upload(fileName, file, request.duration)

        val post = R2dbcPost(
            url = url,
            thumbnailUrl = "Here! Yerim!",
            description = request.description,
            userId = user.id,
        )
        postRepository.save(post).awaitSingle()
    }

    private fun generateFileName(date: String, name: String): String { // 얘는 따로 넘기는 게 나을 것 같음
        return date + "_" + NanoIdUtils.randomNanoId(
            NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
            CUSTOM_STRING.toCharArray(),
            ID_LENGTH,
        ) + "-" + name
    }

    companion object {
        private const val ID_LENGTH: Int = 9
        private const val CUSTOM_STRING = "1234567890"
    }
}
