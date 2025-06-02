package nmnb.webflux.global.infrastructure.external.redis.dto

data class ThumbnailJobPayload(
    val postId: Long,
    val fileName: String,
)
