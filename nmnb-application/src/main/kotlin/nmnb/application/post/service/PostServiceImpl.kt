package nmnb.application.post.service

import nmnb.application.post.service.dto.request.PostPageServiceRequest
import nmnb.application.post.service.dto.response.PostInfoResponse
import nmnb.application.post.service.dto.response.PostPageResponse
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Transactional(readOnly = true)
@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
) : PostService {
    override fun getPostPage(request: PostPageServiceRequest): PostPageResponse {
        val allPostIds = fetchAllPostIds()
        val shuffledPostIds = shufflePostIds(allPostIds, request.seed)

        val startIndex = request.cursor + 1
        val extractedIds = extractPageIds(shuffledPostIds, startIndex, request.size)

        return toPostPageResponse(extractedIds, shuffledPostIds, startIndex)
    }

    private fun toPostPageResponse(
        pageIds: List<Long>,
        shuffledPostIds: List<Long>,
        startIndex: Int,
    ): PostPageResponse {
        val postsInfoResponse = mapPostsToResponse(pageIds)
        val hasNext = shuffledPostIds.size > (startIndex + pageIds.size)
        val nextCursor = startIndex + pageIds.size - 1

        return PostPageResponse.of(postsInfoResponse, hasNext, nextCursor)
    }

    private fun fetchAllPostIds(): List<Long> {
        return postRepository.findAllPostId()
    }

    private fun shufflePostIds(allPostIds: List<Long>, seed: Int): List<Long> {
        return allPostIds.shuffled(Random(seed))
    }

    private fun extractPageIds(shuffledPostIds: List<Long>, startIndex: Int, size: Int): List<Long> {
        return shuffledPostIds.drop(startIndex).take(size)
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
