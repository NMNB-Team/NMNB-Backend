package nmnb.webflux.global.infrastructure.external.redis.dto

import nmnb.common.domain.AccessStrategy

data class ThumbnailJobPayload(
    val postId: Long,
    val fileName: String,
    val accessStrategy: AccessStrategy,
)
