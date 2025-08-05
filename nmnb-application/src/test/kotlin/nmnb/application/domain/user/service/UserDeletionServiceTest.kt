package nmnb.application.domain.user.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import nmnb.application.IntegrationTestSupport
import nmnb.domain.like.Like
import nmnb.domain.like.repository.LikeRepository
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UserDeletionServiceTest : IntegrationTestSupport() {

    @Autowired
    private lateinit var userDeletionService: UserDeletionService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var likeRepository: LikeRepository

    @PersistenceContext
    private lateinit var em: EntityManager

    @AfterEach
    fun tearDown() {
        likeRepository.deleteAllInBatch()
        postRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @Test
    @Transactional
    @DisplayName("회원 탈퇴 시 유저가 데이터베이스에서 삭제된다. ")
    fun withdrawWithHardDelete_removeUser() {
        // given
        val user = User.fixture()
        userRepository.save(user)

        // when
        userDeletionService.hardDeleteUser(user.id!!)

        em.flush()
        em.clear()

        // then
        Assertions.assertThat(userRepository.findById(user.id!!)).isEmpty
    }

    @Test
    @Transactional
    @DisplayName("회원 탈퇴 시 유저가 게시한 영상은 데이터베이스에서 삭제된다. ")
    fun withdrawWithHardDelete_removeUserPosts() {
        // given
        val user1 = User.fixture()
        val user2 = User.fixture()
        userRepository.saveAll(listOf(user1, user2))

        val post1 = Post.fixture(user = user1)
        val post2 = Post.fixture(user = user1)
        val post3 = Post.fixture(user = user2)
        postRepository.saveAll(listOf(post1, post2, post3))

        // when
        userDeletionService.hardDeleteUser(user1.id!!)

        em.flush()
        em.clear()

        // then
        val posts = em.createQuery("SELECT p FROM Post p WHERE p.user.id = :id", Post::class.java)
            .setParameter("id", user1.id)
            .resultList
        Assertions.assertThat(posts).isEmpty()

        Assertions.assertThat(postRepository.findById(post1.id!!)).isEmpty
        Assertions.assertThat(postRepository.findById(post3.id!!)).isPresent
    }

    @Test
    @Transactional
    @DisplayName("회원 탈퇴 시 유저가 게시글에 누른 좋아요는 데이터베이스에서 삭제된다. ")
    fun withdrawWithHardDelete_removeUserLikes() {
        // given
        val user1 = User.fixture()
        val user2 = User.fixture()
        userRepository.saveAll(listOf(user1, user2))

        val post1 = Post.fixture(user = user1)
        val post2 = Post.fixture(user = user1)
        val post3 = Post.fixture(user = user2)
        postRepository.saveAll(listOf(post1, post2, post3))

        likeRepository.saveAll(
            listOf(
                Like.fixture(user1, post1),
                Like.fixture(user1, post2),
                Like.fixture(user1, post3),
                Like.fixture(user2, post1),
                Like.fixture(user2, post3),
            ),
        )

        // when
        userDeletionService.hardDeleteUser(user1.id!!)

        // then
        val likes = em.createNativeQuery("SELECT * FROM user_post_likes l WHERE l.user_id = :id")
            .setParameter("id", user1.id)
            .resultList
        Assertions.assertThat(likes).isEmpty()

        Assertions.assertThat(likeRepository.findAll().size).isEqualTo(1)
    }
}
