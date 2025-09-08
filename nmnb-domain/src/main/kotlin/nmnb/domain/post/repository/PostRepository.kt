package nmnb.domain.post.repository

import io.lettuce.core.dynamic.annotation.Param
import nmnb.domain.post.Post
import nmnb.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long>, PostRepositoryCustom {
    @Query("SELECT p.id FROM Post p")
    fun findAllPostId(): List<Long>

    fun findAllByIdIn(ids: Iterable<Long>): List<Post>

    @Query("SELECT p.id FROM Post p WHERE p.user.id IN :userIds")
    fun findPostIdsByUserIds(@Param("userIds") userIds: List<String>): List<Long>

    @Query("SELECT MAX(p.id) FROM Post p WHERE p.user = :user")
    fun findMaxIdByUser(user: User): Long?
}
