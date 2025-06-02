package nmnb.webflux.global.infrastructure.external.redis

data class ThumbnailJobPayload(
    val postId: Long,
    val fileName: String,
)
