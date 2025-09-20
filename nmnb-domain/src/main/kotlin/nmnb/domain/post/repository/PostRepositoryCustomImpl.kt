package nmnb.domain.post.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import nmnb.domain.post.Post
import nmnb.domain.post.QPost.post
import nmnb.domain.post.SortType
import nmnb.domain.user.User
import org.springframework.stereotype.Repository

@Repository
class PostRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : PostRepositoryCustom {
    override fun findPostsByCursor(
        user: User,
        cursorId: Long,
        sortType: SortType,
        size: Int,
    ): List<Post> {
        val builder = BooleanBuilder()
        builder.and(post.user.eq(user))

        when (sortType) {
            SortType.RECENT -> builder.and(post.id.lt(cursorId))
            SortType.OLDEST -> builder.and(post.id.gt(cursorId))
        }

        val orderSpecifiers = when (sortType) {
            SortType.RECENT -> arrayOf(post.id.desc())
            SortType.OLDEST -> arrayOf(post.id.asc())
        }

        return queryFactory.selectFrom(post)
            .where(builder)
            .orderBy(*orderSpecifiers)
            .limit(size.toLong() + 1)
            .fetch()
    }
}
