package nmnb.application.domain.user.service

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserDeletionService(
    private val entityManager: EntityManager,
) {
    @Transactional
    fun hardDeleteUser(userId: String) {
        // 유저가 누른 좋아요 삭제
        entityManager.createNativeQuery("DELETE FROM user_post_likes WHERE user_id = :id")
            .setParameter("id", userId)
            .executeUpdate()

        // 유저가 작성한 게시글에 달린 좋아요 삭제
        entityManager.createNativeQuery("DELETE FROM user_post_likes WHERE post_id IN (SELECT post_id FROM posts WHERE user_id = :id)")
            .setParameter("id", userId)
            .executeUpdate()

        // 유저가 작성한 게시글 삭제
        entityManager.createNativeQuery("DELETE FROM posts WHERE user_id = :id")
            .setParameter("id", userId)
            .executeUpdate()

        // 유저 삭제
        entityManager.createNativeQuery("DELETE FROM users WHERE user_id = :id")
            .setParameter("id", userId)
            .executeUpdate()
    }
}
