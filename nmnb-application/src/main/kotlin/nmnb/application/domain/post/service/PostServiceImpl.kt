package nmnb.application.domain.post.service

import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest
import nmnb.application.domain.post.service.dto.response.PostInfoResponse
import nmnb.application.domain.post.service.dto.response.PostPageResponse
import nmnb.application.domain.post.utils.RandomSelector
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val postCacheService: PostCacheService,
) : PostService {
    override fun getPostPage(request: PostPageServiceRequest): PostPageResponse {
        postCacheService.refreshPostcache(request)
        val allPostIds = postCacheService.getAllPostIds()
        val shuffledPostIds = postCacheService.getShuffledIds(allPostIds, request.seed)

        val startIndex = request.cursor + 1
        val extractedIds = RandomSelector.extractPageIds(shuffledPostIds, startIndex, request.size)

        return toPostPageResponse(extractedIds, shuffledPostIds, startIndex)
    }

    private fun toPostPageResponse(
        pageIds: List<Long>,
        shuffledPostIds: List<Long>,
        startIndex: Int,
    ): PostPageResponse {
        val postsInfoResponse = mapPostsToResponse(pageIds)
        val hasNext = shuffledPostIds.size > (startIndex + pageIds.size)
        val nextCursor = if (!hasNext) -1 else startIndex + pageIds.size - 1

        return PostPageResponse.of(postsInfoResponse, hasNext, nextCursor)
    }

    private fun mapPostsToResponse(pageIds: List<Long>): List<PostInfoResponse> {
        val postsMap = fetchPostsByIds(pageIds)
        return pageIds.map { id ->
            PostInfoResponse.of(postsMap[id]!!)
        }
    }

    private fun fetchPostsByIds(pageIds: List<Long>): Map<Long, Post> {
        val posts = postRepository.findAllByIdIn(pageIds)
        return posts.associateBy { it.id!! }
    }
}
