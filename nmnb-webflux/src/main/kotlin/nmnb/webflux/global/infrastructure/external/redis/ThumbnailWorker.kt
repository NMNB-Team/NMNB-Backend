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
import java.io.File

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
                        logger.info("Received payload from queue---------")
                        processPayload(payload)
                        logger.info("Finished payload from queue---------")
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

        val thumbnailFile = createThumbnail(fileName)
        logger.info("Thumbnail created for file: $fileName")

        val thumbnailUrl = s3Service.uploadThumbnail(fileName, thumbnailFile, accessStrategy)
        logger.info("Thumbnail updated to S3: $thumbnailUrl")

        updatePostThumbnail(postId, thumbnailUrl)
        logger.info("Post updated with thumbnail url")

        if (thumbnailFile.delete()) {
            logger.info("Temporary thumbnail file deleted : ${thumbnailFile.absolutePath}")
        } else {
            logger.warn("Failed to delete temporary thumbnail file : ${thumbnailFile.absolutePath}")
        }
    }

    private suspend fun createThumbnail(fileName: String): File =
        coroutineScope {
            logger.info("Starting thumbnail creation for video file : $fileName")
            val videoDownload = async { s3Service.download(fileName) }
            val thumbnailGeneration = async {
                val localVideoFile = videoDownload.await()
                ffmpegService.createThumbnail(localVideoFile)
            }

            val thumbnailFile = thumbnailGeneration.await()
            logger.info("Thumbnail generation completed for file : $fileName")

            thumbnailFile
        }

    private suspend fun updatePostThumbnail(postId: Long, thumbnailUrl: String) {
        val post = postRepository.findById(postId).awaitSingleOrNull()
        if (post != null) {
            val updated = post.updateThumbnail(thumbnailUrl)
            postRepository.save(updated).awaitSingle()
            logger.info("post $postId successfully updated")
        } else {
            logger.warn("Post $postId not found")
        }
    }

    companion object {
        private const val QUEUE_KEY = "thumbnail:queue"
    }
}
