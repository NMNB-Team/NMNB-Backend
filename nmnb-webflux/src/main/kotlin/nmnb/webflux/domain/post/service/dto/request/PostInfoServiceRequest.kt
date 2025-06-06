package nmnb.webflux.domain.post.service.dto.request

import nmnb.common.domain.AccessStrategy

data class PostInfoServiceRequest(
    val description: String?,
    val duration: Int,
    val accessStrategy: AccessStrategy,
)
