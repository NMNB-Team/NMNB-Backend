package nmnb.application.domain.report.service

import nmnb.application.IntegrationTestSupport
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.report.PostReport
import nmnb.domain.report.repository.ReportRepository
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

class ReportServiceConcurrencyTest : IntegrationTestSupport() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var reportRepository: ReportRepository

    @Autowired
    private lateinit var postReportService: PostReportService

    @AfterEach
    fun tearDown() {
        reportRepository.deleteAllInBatch()
        postRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @DisplayName("동일 게시글 신고 요청 시 중복 등록되지 않는다.")
    @Test
    fun report() {
        // given
        val threadCount = 10

        val poster = User.fixture()
        val reporter = User.fixture()
        userRepository.saveAll(listOf(poster, reporter))

        val post = Post.fixture(user = poster)
        postRepository.save(post)

        val report = PostReport.fixture(post.id!!, reporter)
        reportRepository.save(report)

        // when
        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        try {
            repeat(threadCount) {
                executorService.submit {
                    try {
                        postReportService.save(report)
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
        val allReport = reportRepository.findAll()
        assertThat(allReport).hasSize(1)
    }
}
