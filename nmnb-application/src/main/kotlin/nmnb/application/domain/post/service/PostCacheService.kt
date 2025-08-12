package nmnb.application.domain.post.service

import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest

interface PostCacheService {
    fun getAllPostIds(): List<Long>
    fun getShuffledIds(ids: List<Long>, seed: Int): List<Long>
    fun refreshPostcache(request: PostPageServiceRequest)
}
