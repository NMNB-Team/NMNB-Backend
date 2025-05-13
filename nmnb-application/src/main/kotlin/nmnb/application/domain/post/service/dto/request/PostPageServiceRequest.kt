package nmnb.application.domain.post.service.dto.request

data class PostPageServiceRequest(
    val seed: Int,
    val cursor: Int,
    val size: Int,
)
