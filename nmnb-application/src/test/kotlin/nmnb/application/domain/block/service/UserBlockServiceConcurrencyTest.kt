package nmnb.application.domain.block.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.block.service.dto.request.UserBlockServiceRequest
import nmnb.domain.block.repository.BlockRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UserBlockServiceConcurrencyTest : IntegrationTestSupport() {
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

    @DisplayName("동일 사용자 차단 요청 시 중복 등록도지 않는다.")
    @Test
    fun block() {
        // given
        val threadCount = 10

        val blockedUser = User.fixture()
        val blocker = User.fixture()
        userRepository.saveAll(listOf(blockedUser, blocker))

        val request = UserBlockServiceRequest(blockedUser.id!!)

        val blockerInit = userRepository.findById(blocker.id!!).get()
        val blockedUserInit = userRepository.findById(blockedUser.id!!).get()

        // when
        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        try {
            repeat(threadCount) {
                executorService.submit {
                    try {
                        userBlockService.block(blockerInit, request)
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
        } finally {
            executorService.shutdown()
            executorService.awaitTermination(1, TimeUnit.MINUTES)
        }

        // then
        val allBlocks = blockRepository.findAll()
        assertThat(allBlocks).hasSize(1)

        val savedBlock = allBlocks[0]
        assertThat(savedBlock.blockedUser.id).isEqualTo(blockedUserInit.id)
        assertThat(savedBlock.blocker.id).isEqualTo(blockerInit.id)
    }
}
