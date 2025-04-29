package nmnb.domain.like.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import nmnb.domain.like.QLike.like
import org.springframework.stereotype.Repository

@Repository
class LikeRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : LikeRepositoryCustom {
    override fun delete(userId: String, postId: Long) {
        queryFactory
            .delete(like)
            .where(
                like.likedBy.id.eq(userId)
                    .and(like.post.id.eq(postId)),
            )
            .execute()
    }
}
