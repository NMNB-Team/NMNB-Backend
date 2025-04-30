package nmnb.application.post.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.post.service.dto.request.PostPageServiceRequest
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@Transactional
class PostServiceImplTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val postRepository: PostRepository,
    @Autowired private val postService: PostService,
) : IntegrationTestSupport() {

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
        val result1 = postService.getPostPage(request1)
        val request2 = PostPageServiceRequest(seed = seed, cursor = result1.nextCursor!!, size = size)
        val result2 = postService.getPostPage(request2)

        val allResult = (result1.postInfo + result2.postInfo)

        // then
        assertThat(result1.postInfo).hasSize(2)
        assertThat(result1.nextCursor).isEqualTo(1)
        assertThat(result1.hasNext).isTrue

        assertThat(result2.postInfo).hasSize(1)
        assertThat(result2.nextCursor).isEqualTo(2)
        assertThat(result2.hasNext).isFalse

        assertThat(allResult).hasSize(3)
            .extracting("url")
            .containsExactlyInAnyOrder("url1", "url2", "url3")
    }
}
