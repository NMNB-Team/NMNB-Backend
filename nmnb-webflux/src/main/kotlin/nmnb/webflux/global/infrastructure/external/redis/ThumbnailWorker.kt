package nmnb.webflux.global.infrastructure.external.redis

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import nmnb.r2dbc.post.R2dbcPostRepository
import nmnb.webflux.global.infrastructure.external.ffmpeg.FfmpegService
import nmnb.webflux.global.infrastructure.external.redis.dto.ThumbnailJobPayload
import nmnb.webflux.global.infrastructure.external.s3.S3Service
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class ThumbnailWorker(
    private val postRepository: R2dbcPostRepository,
    private val ffmpegService: FfmpegService,
    private val s3Service: S3Service,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    private val logger = LoggerFactory.getLogger(ThumbnailWorker::class.java)
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
                        .leftPop(QUEUE_KEY)
                        .awaitSingleOrNull()

                    if (payload != null) {
                        processPayload(payload)
                    } else {
                        delay(1000)
                    }
                } catch (e: Exception) {
                    logger.error("ThumbnailWorker error: ${e.message}", e)
                    delay(1000)
                }
            }
        }
    }

    private suspend fun processPayload(payload: String) = coroutineScope {
        val job = objectMapper.readValue(payload, ThumbnailJobPayload::class.java)
        val postId = job.postId
        val fileName = job.fileName
        val accessStrategy = job.accessStrategy

        val downloadDeferred = async { s3Service.download(fileName) }
        val thumbnailDeferred = async {
            val localVideoFile = downloadDeferred.await()
            ffmpegService.createThumbnail(localVideoFile)
        }

        val localVideoFile = downloadDeferred.await()
        val thumbnailFile = thumbnailDeferred.await()
        val thumbnailUrl = s3Service.uploadThumbnail(fileName, thumbnailFile, accessStrategy)

        val post = postRepository.findById(postId).awaitSingleOrNull()
        post?.let {
            val updated = it.updateThumbnail(thumbnailUrl)
            postRepository.save(updated).awaitSingle()
        }

        localVideoFile.delete()
        thumbnailFile.delete()
    }

    companion object {
        private const val QUEUE_KEY = "thumbnail:queue"
    }
}
