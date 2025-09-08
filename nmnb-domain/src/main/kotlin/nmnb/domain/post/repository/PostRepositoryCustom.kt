package nmnb.domain.post.repository

import nmnb.domain.post.Post
import nmnb.domain.post.SortType
import nmnb.domain.user.User

interface PostRepositoryCustom {
    fun findPostsByCursor(user: User, cursorId: Long, sortType: SortType, size: Int): List<Post>
}
