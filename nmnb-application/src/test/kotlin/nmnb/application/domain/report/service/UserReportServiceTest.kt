package nmnb.application.domain.report.service

import nmnb.application.IntegrationTestSupport
import nmnb.application.domain.report.service.dto.request.UserReportServiceRequest
import nmnb.common.response.exception.ReportException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.report.ContentType
import nmnb.domain.report.UserReport
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class UserReportServiceTest : IntegrationTestSupport() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var reportRepository: ReportRepository

    @Autowired
    private lateinit var userReportService: UserReportService

    @AfterEach
    fun tearDown() {
        reportRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @DisplayName("사용자를 신고한다.")
    @Test
    fun userReport() {
        // given
        val target = User.fixture()
        val reporter = User.fixture()
        userRepository.saveAll(listOf(target, reporter))

        val request = UserReportServiceRequest(target.id!!, ContentType.SEXUAL)

        // when
        userReportService.report(reporter, request)

        // then
        Assertions.assertThat(reportRepository.findAll()).hasSize(1)
    }

    @DisplayName("본인을 신고할 시 예외가 발생한다.")
    @Test
    fun userReportSelf() {
        // given
        val reporter = User.fixture()
        userRepository.save(reporter)

        val request = UserReportServiceRequest(reporter.id!!, ContentType.SEXUAL)

        // when
        val exception = assertThrows<ReportException> {
            userReportService.validateReport(reporter, request)
        }

        // then
        Assertions.assertThat(exception.getCode()).isEqualTo(ErrorStatus.CANNOT_REPORT_SELF)
    }

    @DisplayName("사용자를 중복 신고할 시 예외가 발생한다.")
    @Test
    fun userReportDuplicate() {
        // given
        // given
        val poster = User.fixture()
        val reporter = User.fixture()
        userRepository.saveAll(listOf(poster, reporter))

        val report = UserReport.fixture(poster.id!!, reporter)
        reportRepository.save(report)

        val request = UserReportServiceRequest(poster.id!!, ContentType.SEXUAL)

        // when
        val exception = assertThrows<ReportException> {
            userReportService.validateReport(reporter, request)
        }

        // then
        Assertions.assertThat(exception.getCode()).isEqualTo(ErrorStatus.ALREADY_REPORTED)
        Assertions.assertThat(reportRepository.findAll()).hasSize(1)
    }
}
