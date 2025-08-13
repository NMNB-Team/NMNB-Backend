package nmnb.application.domain.report.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.report.service.dto.request.PostReportServiceRequest
import nmnb.common.response.exception.ReportException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.report.ContentType
import nmnb.domain.report.PostReport
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class PostReportServiceTest : IntegrationTestSupport() {
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

    @DisplayName("게시글을 신고한다.")
    @Test
    fun postReport() {
        // given
        val poster = User.fixture()
        val reporter = User.fixture()
        userRepository.saveAll(listOf(poster, reporter))

        val post = Post.fixture(user = poster)
        postRepository.save(post)

        val request = PostReportServiceRequest(post.id!!, ContentType.SEXUAL)

        // when
        postReportService.report(reporter, request)

        // then
        assertThat(reportRepository.findAll()).hasSize(1)
    }

    @DisplayName("중복 신고시 예외가 발생한다.")
    @Test
    fun postReportDuplicate() {
        // given
        val poster = User.fixture()
        val reporter = User.fixture()
        userRepository.saveAll(listOf(poster, reporter))

        val post = Post.fixture(user = poster)
        postRepository.save(post)

        val report = PostReport.fixture(post.id!!, reporter)
        reportRepository.save(report)

        val request = PostReportServiceRequest(post.id!!, ContentType.SEXUAL)

        // when
        val exception = assertThrows<ReportException> {
            postReportService.validateReport(reporter, request)
        }

        // then
        assertThat(exception.getCode()).isEqualTo(ErrorStatus.ALREADY_REPORTED)
        assertThat(reportRepository.findAll()).hasSize(1)
    }

    @DisplayName("본인의 게시글을 신고할 시 예외가 발생한다.")
    @Test
    fun postReportSelf() {
        // given
        val reporter = User.fixture()
        userRepository.save(reporter)

        val post = Post.fixture(user = reporter)
        postRepository.save(post)

        val request = PostReportServiceRequest(post.id!!, ContentType.SEXUAL)

        // when
        val exception = assertThrows<ReportException> {
            postReportService.validateReport(reporter, request)
        }

        // then
        assertThat(exception.getCode()).isEqualTo(ErrorStatus.CANNOT_REPORT_SELF)
    }
}
