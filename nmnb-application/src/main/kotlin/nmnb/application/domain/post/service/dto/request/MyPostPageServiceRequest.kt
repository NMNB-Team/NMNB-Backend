package nmnb.application.domain.post.service.dto.request

import nmnb.domain.post.SortType

data class MyPostPageServiceRequest(
    val cursorId: Long,
    val size: Int,
    val sortType: SortType,
)
