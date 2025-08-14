package nmnb.application.domain.block.service

import nmnb.application.domain.block.service.dto.request.UserBlockServiceRequest
import nmnb.common.response.exception.BlockException
import nmnb.common.response.exception.UserException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.block.Block
import nmnb.domain.block.repository.BlockRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserBlockService(
    private val blockRepository: BlockRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun block(user: User, request: UserBlockServiceRequest) {
        val blockedUser = getBlockedUser(request)

        try {
            blockRepository.save(Block(blocker = user, blockedUser = blockedUser))
        } catch (e: DataIntegrityViolationException) {
            throw BlockException(ErrorStatus.ALREADY_BLOCKED)
        }
    }

    @Transactional
    fun unBlock(user: User, request: UserBlockServiceRequest) {
        val blockedUser = getBlockedUser(request)
        blockRepository.findByBlockerAndBlockedUser(user, blockedUser)
            ?.let { block ->
                blockRepository.delete(block)
            }
    }

    private fun getBlockedUser(request: UserBlockServiceRequest): User =
        userRepository.findById(request.userId).orElseThrow {
            UserException(ErrorStatus.USER_NOT_FOUND)
        }
}
