package nmnb.application.domain.post.service

import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest
import nmnb.application.domain.post.service.dto.response.PostInfoResponse
import nmnb.application.domain.post.service.dto.response.PostPageResponse
import nmnb.application.domain.post.utils.RandomSelector
import nmnb.domain.block.repository.BlockRepository
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val blockRepository: BlockRepository,
    private val postCacheService: PostCacheService,
) : PostService {
    override fun getPostPage(userId: String?, request: PostPageServiceRequest): PostPageResponse {
        postCacheService.refreshPostcache(request)

        val filteredPostIds = getFilteredPostIds(userId)
        val shuffledPostIds = postCacheService.getShuffledIds(filteredPostIds, request.seed)

        val startIndex = request.cursor + 1
        val extractedIds = RandomSelector.extractPageIds(shuffledPostIds, startIndex, request.size)

        return toPostPageResponse(extractedIds, shuffledPostIds, startIndex)
    }

    private fun getFilteredPostIds(userId: String?): List<Long> {
        val blockedUserIds = getBlockedUserIds(userId)
        val blockedPostIds = getBlockedPostIds(blockedUserIds)

        val allPostIds = postCacheService.getAllPostIds()

        return allPostIds.filter { id -> !blockedPostIds.contains(id) }
    }

    private fun getBlockedPostIds(blockedUserIds: List<String>) = if (blockedUserIds.isNotEmpty()) {
        postRepository.findPostIdsByUserIds(blockedUserIds)
    } else {
        emptyList()
    }

    private fun getBlockedUserIds(userId: String?) = userId?.let {
        blockRepository.findByBlockerId(userId)
    } ?: emptyList()

    private fun toPostPageResponse(
        pageIds: List<Long>,
        shuffledPostIds: List<Long>,
        startIndex: Int,
    ): PostPageResponse {
        val postsInfoResponse = mapPostsToResponse(pageIds)
        val hasNext = shuffledPostIds.size > (startIndex + pageIds.size)
        val nextCursor = if (!hasNext) INITIAL_CURSOR else startIndex + pageIds.size - 1

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

    companion object {
        private const val INITIAL_CURSOR = -1
    }
}
