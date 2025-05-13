package nmnb.application.domain.like.service

import nmnb.application.domain.post.exception.PostException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.like.Like
import nmnb.domain.like.repository.LikeRepository
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.user.User
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class LikeAsyncServiceImpl(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
) : LikeAsyncService {

    @Transactional
    @Async
    override fun saveLikeToDB(
        user: User,
        postId: Long,
    ) {
        val post =
            postRepository.findById(postId)
                .orElseThrow { (throw PostException(ErrorStatus.POST_NOTFOUND)) }
        likeRepository.save(Like(user, post))
    }

    @Transactional
    @Async
    override fun deleteLikeToDB(
        user: User,
        postId: Long,
    ) {
        likeRepository.delete(user.id!!, postId)
    }
}
