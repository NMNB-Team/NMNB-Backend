package nmnb.domain.post.repository

import nmnb.domain.post.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long> {
    @Query("SELECT p.id FROM Post p")
    fun findAllPostId(): List<Long>

    @Query("SELECT p FROM Post p WHERE p.id IN :ids")
    fun findAllByIdIn(ids: List<Long>): List<Post>
}
