package nmnb.domain.like.repository

import io.lettuce.core.dynamic.annotation.Param
import nmnb.domain.like.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface LikeRepository : JpaRepository<Like, Long>, LikeRepositoryCustom {
    @Modifying
    @Query("delete from Like l where l.post.id = :postId")
    fun deleteAllByPostId(@Param("postId") postId: Long)
    fun findAllByPostId(postId: Long): List<Like>
}
