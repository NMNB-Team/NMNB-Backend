package nmnb.webflux.global.infrastructure.external

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import nmnb.r2dbc.post.R2dbcPostRepository
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class ThumbnailWorker(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val ffmpegService: FfmpegService,
    private val s3Service: S3Service,
    private val postRepository: R2dbcPostRepository,
) {

    private val scope = CoroutineScope(Dispatchers.Default)

    @PostConstruct
    fun init() {
        start()
    }

    fun start() {
        scope.launch {
            while (isActive) {
                try {
                    val payload = redisTemplate.opsForList()
                        .leftPop("thumbnail:queue")
                        .awaitSingleOrNull()

                    if (payload != null) {
                        processPayload(payload)
                    } else {
                        delay(1000) // 큐가 비어있으면 1초 대기
                    }
                } catch (e: Exception) {
                    println("ThumbnailWorker error: ${e.message}")
                    delay(1000) // 에러 발생 시 잠시 대기 후 재시도
                }
            }
        }
    }

    private suspend fun processPayload(payload: String) {
        val (postIdStr, fileName) = payload.split("|")
        val postId = postIdStr.toLong()

        val localVideoFile = s3Service.download(fileName)

        val thumbnailFile = ffmpegService.createThumbnail(localVideoFile)
        val thumbnailFileName = "thumbnail/$fileName.jpg"

        val thumbnailUrl = s3Service.uploadThumbnail(thumbnailFileName, thumbnailFile)

        val post = postRepository.findById(postId).awaitSingleOrNull()
        post?.let {
            val updated = it.updateThumbnail(thumbnailUrl)
            postRepository.save(updated).awaitSingle()
        }

        localVideoFile.delete()
        thumbnailFile.delete()
    }
}
