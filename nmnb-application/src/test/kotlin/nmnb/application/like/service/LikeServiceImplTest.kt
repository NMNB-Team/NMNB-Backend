package nmnb.application.like.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.like.service.dto.request.PostLikeServiceRequest
import nmnb.domain.like.LikeCacheKey
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.user.User
import nmnb.domain.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.annotation.Transactional

@Transactional
class LikeServiceImplTest(
    @Autowired
    private val postRepository: PostRepository,
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val likeService: LikeService,
    @Autowired
    private val redisTemplate: StringRedisTemplate,
) : IntegrationTestSupport() {

    @MockBean
    private lateinit var likeAsyncService: LikeAsyncService

    @DisplayName("해당 Post에 좋아요가 없는 경우 좋아요를 등록합니다.")
    @Test
    fun like() {
        // given
        val likedBy = User.fixture(email = "email1@gmail.com")
        val postingBy = User.fixture(email = "email2@gmail.com")
        userRepository.saveAll(listOf(likedBy, postingBy))
        val post = Post.fixture(user = postingBy)
        postRepository.save(post)

        val request = PostLikeServiceRequest(likedBy, post.id!!)

        // when
        likeService.likeOrUnlike(likedBy, request)

        // then
        val key = LikeCacheKey(likedBy.id!!, post.id!!).key
        val redisResult = redisTemplate.hasKey(key)

        assertThat(redisResult).isTrue
        verify(likeAsyncService, times(1)).saveLikeToDB(likedBy, post.id!!)
    }

    @DisplayName("해당 Post에 좋아요가 있는 경우 좋아요를 취소합니다.")
    @Test
    fun unLike() {
        // given
        val likedBy = User.fixture(email = "email3@gmail.com")
        val postingBy = User.fixture(email = "email4@gmail.com")
        userRepository.saveAll(listOf(likedBy, postingBy))
        val post = Post.fixture(user = postingBy)
        postRepository.save(post)
        val request = PostLikeServiceRequest(likedBy, post.id!!)

        val cacheKey = LikeCacheKey(likedBy.id!!, post.id!!).key
        redisTemplate.opsForValue().set(cacheKey, "1")

        // when
        likeService.likeOrUnlike(likedBy, request)

        // then
        assertThat(redisTemplate.hasKey(cacheKey)).isFalse
        verify(likeAsyncService, times(1)).deleteLikeToDB(likedBy, post.id!!)
    }
}
