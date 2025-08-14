package nmnb.application.domain.block.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.block.service.dto.request.UserBlockServiceRequest
import nmnb.common.response.exception.UserException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.block.Block
import nmnb.domain.block.repository.BlockRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class UserBlockServiceTest : IntegrationTestSupport() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var blockRepository: BlockRepository

    @Autowired
    private lateinit var userBlockService: UserBlockService

    @AfterEach
    fun tearDown() {
        blockRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @DisplayName("사용자를 차단하는데 성공한다.")
    @Test
    fun block() {
        // given
        val blockedUser = User.fixture()
        val blocker = User.fixture()
        userRepository.saveAll(listOf(blockedUser, blocker))

        val request = UserBlockServiceRequest(blockedUser.id!!)

        // when
        userBlockService.block(blocker, request)

        // then
        assertThat(blockRepository.findByBlockerId(blocker.id!!)).hasSize(1)
    }

    @DisplayName("차단을 요청한 사용자가 존재하지 않을 경우 예외가 발생한다.")
    @Test
    fun blockFailedWhenBlockedUserNotExist() {
        // given
        val blocker = User.fixture()
        userRepository.save(blocker)

        val request = UserBlockServiceRequest("temp")

        // when
        val exception = assertThrows<UserException> { userBlockService.block(blocker, request) }

        // then
        assertThat(exception.getCode()).isEqualTo(ErrorStatus.USER_NOT_FOUND)
    }

    @DisplayName("사용자 차단 해제에 성공한다.")
    @Test
    fun unblock() {
        // given
        val blocker = User.fixture()
        val blockedUser = User.fixture()
        userRepository.saveAll(listOf(blocker, blockedUser))

        blockRepository.save(Block.fixture(blocker, blockedUser))

        // then
        assertThat(blockRepository.findByBlockerId(blocker.id!!)).hasSize(1)

        // given
        val request = UserBlockServiceRequest(blockedUser.id!!)

        // when
        userBlockService.unBlock(blocker, request)

        // then
        assertThat(blockRepository.findByBlockerId(blocker.id!!)).isEmpty()
    }
}
