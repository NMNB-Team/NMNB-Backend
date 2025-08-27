package nmnb.application.domain.post.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest
import nmnb.common.response.exception.PostException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.block.Block
import nmnb.domain.block.repository.BlockRepository
import nmnb.domain.like.Like
import nmnb.domain.like.repository.LikeRepository
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cache.CacheManager
import org.springframework.transaction.annotation.Transactional

@Transactional
class PostServiceImplTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val postRepository: PostRepository,
    @Autowired private val blockRepository: BlockRepository,
    @Autowired private val likeRepository: LikeRepository,
    @Autowired private val postService: PostService,
    @Autowired private var cacheManager: CacheManager,
) : IntegrationTestSupport() {

    @MockBean
    private lateinit var postCacheEvictor: PostCacheEvictor

    @AfterEach
    fun tearDown() {
        likeRepository.deleteAllInBatch()
        blockRepository.deleteAllInBatch()
        postRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()

        cacheManager.getCache("postIds")?.clear()
        cacheManager.getCache("shuffledIds")?.clear()
    }

    @DisplayName("게시글을 랜덤 페이지 조회 시, 여러 번 요청하여 전체 게시글을 모두 조회할 수 있다")
    @Test
    fun getPostsPage() {
        // given
        val viewer = User.fixture()
        val writer = User.fixture(email = "user2@email.com")
        userRepository.saveAll(listOf(viewer, writer))

        val post1 = Post.fixture(url = "url1", user = writer)
        val post2 = Post.fixture(url = "url2", user = writer)
        val post3 = Post.fixture(url = "url3", user = writer)
        postRepository.saveAll(listOf(post1, post2, post3))

        val seed = 1234
        val size = 2

        // when
        val request1 = PostPageServiceRequest(seed = seed, cursor = -1, size = size)
        val result1 = postService.getPostPage(null, request1)
        val request2 = PostPageServiceRequest(seed = seed, cursor = result1.nextCursor!!, size = size)
        val result2 = postService.getPostPage(null, request2)

        val allResult = (result1.postInfo + result2.postInfo)

        // then
        assertThat(result1.postInfo).hasSize(2)
        assertThat(result1.nextCursor).isEqualTo(1)
        assertThat(result1.hasNext).isTrue

        assertThat(result2.postInfo).hasSize(1)
        assertThat(result2.hasNext).isFalse

        assertThat(allResult).hasSize(3)
            .extracting("url")
            .containsExactlyInAnyOrder("url1", "url2", "url3")
    }

    @DisplayName("마지막 페이지를 조회할 경우, cursor 값은 -1이다.")
    @Test
    fun getPostsLastPage() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        val post1 = Post.fixture(url = "url1", user = user)
        val post2 = Post.fixture(url = "url2", user = user)
        postRepository.saveAll(listOf(post1, post2))

        val seed = 1234
        val size = 2

        // when
        val request = PostPageServiceRequest(seed = seed, cursor = -1, size = size)
        val result = postService.getPostPage(null, request)

        // then
        assertThat(result.nextCursor).isEqualTo(-1)
    }

    @DisplayName("조회 커서가 초기 값(-1)일 경우 캐시가 무효화된다.")
    @Test
    fun refreshCacheWhenInitialCursor() {
        // given
        val seed = 1234
        val request = PostPageServiceRequest(seed = seed, cursor = -1, size = 7)

        // when
        postService.getPostPage(null, request)

        // then
        verify(postCacheEvictor, times(1)).refreshPostIds()
    }

    @DisplayName("조회 커서가 초기 값(-1)이 아닐 경우 캐시가 무효화되지 않는다.")
    @Test
    fun refreshCacheWhenNotInitialCursor() {
        // given
        val seed = 1234
        val request = PostPageServiceRequest(seed = seed, cursor = 1, size = 7)

        // when
        postService.getPostPage(null, request)

        // then
        verify(postCacheEvictor, never()).refreshPostIds()
    }

    @DisplayName("게시글 조회시, 차단한 사용자의 게시글은 조회되지 않는다.")
    @Test
    fun getPostsPageWithoutBlockUserPosts() {
        // given
        val writer = User.fixture()
        val blocker = User.fixture()
        val blockedUser = User.fixture(email = "user2@email.com")
        userRepository.saveAll(listOf(writer, blocker, blockedUser))

        postRepository.saveAll(
            listOf(
                Post.fixture(url = "url1", user = writer),
                Post.fixture(url = "url2", user = writer),
                Post.fixture(url = "url3", user = blockedUser),
                Post.fixture(url = "url4", user = writer),
                Post.fixture(url = "url5", user = blockedUser),
                Post.fixture(url = "url6", user = writer),
                Post.fixture(url = "url7", user = blockedUser),
            ),
        )

        val seed = 1234
        val size = 5

        blockRepository.save(Block.fixture(blocker, blockedUser))

        // when
        val request = PostPageServiceRequest(seed = seed, cursor = -1, size = size)
        val result = postService.getPostPage(blocker.id, request)

        // then
        assertThat(result.postInfo).hasSize(4)
            .extracting("url")
            .containsExactlyInAnyOrder("url1", "url2", "url4", "url6")
        assertThat(result.nextCursor).isEqualTo(-1)
        assertThat(result.hasNext).isFalse
    }

    @DisplayName("본인이 작성한 게시글을 삭제하는데 성공한다.")
    @Test
    fun deletePost() {
        // given
        val writer = User.fixture()
        val likedBy1 = User.fixture()
        val likedBy2 = User.fixture()
        userRepository.saveAll(listOf(writer, likedBy1, likedBy2))

        val post = Post.fixture(url = "url1", user = writer)
        postRepository.save(post)
        likeRepository.saveAll(
            listOf(
                Like.fixture(likedBy1, post),
                Like.fixture(likedBy2, post),
            ),
        )

        // then
        assertThat(postRepository.findById(post.id!!)).isNotEmpty
        assertThat(likeRepository.findAllByPostId(post.id!!)).size().isEqualTo(2)

        // when
        postService.deletePost(writer.id!!, post.id!!)

        // then
        assertThat(postRepository.findById(post.id!!)).isEmpty
        assertThat(likeRepository.findAllByPostId(post.id!!)).isEmpty()
    }

    @DisplayName("본인이 작성하지 않은 게시글을 삭제하려고 할 때, 예외가 발생한다.")
    @Test
    fun deletePostFailsForNonAuthor() {
        // given
        val writer = User.fixture()
        val likedBy1 = User.fixture()
        val likedBy2 = User.fixture()
        userRepository.saveAll(listOf(writer, likedBy1, likedBy2))

        val post = Post.fixture(url = "url1", user = writer)
        postRepository.save(post)
        likeRepository.saveAll(
            listOf(
                Like.fixture(likedBy1, post),
                Like.fixture(likedBy2, post),
            ),
        )

        // then
        assertThat(postRepository.findById(post.id!!)).isNotEmpty
        assertThat(likeRepository.findAllByPostId(post.id!!)).size().isEqualTo(2)

        // when
        val exception = assertThrows<PostException> {
            postService.deletePost(writer.id!!, post.id!!)
        }

        // then
        assertEquals(ErrorStatus.AUTHOR_MISMATCH, exception.getCode())
    }
}
