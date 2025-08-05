package nmnb.domain.post.repository

import nmnb.domain.post.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository : JpaRepository<Post, Long> {
    @Query("SELECT p.id FROM Post p")
    fun findAllPostId(): List<Long>

    fun findAllByIdIn(ids: Iterable<Long>): List<Post>
}
