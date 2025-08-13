package nmnb.application.domain.report.service

import nmnb.application.domain.report.service.dto.request.UserReportServiceRequest
import nmnb.common.response.exception.ReportException
import nmnb.common.response.exception.UserException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.report.UserReport
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserReportService(
    reportRepository: ReportRepository,
    private val userRepository: UserRepository,
) : ReportService<UserReport, UserReportServiceRequest>(reportRepository) {
    override fun validateReport(user: User, request: UserReportServiceRequest) {
        validateTarget(request.targetId)
        validateNotSelfReport(user.id!!, request.targetId)
    }

    private fun validateTarget(targetId: String): User =
        userRepository.findById(targetId).orElseThrow {
            UserException(ErrorStatus.USER_NOT_FOUND)
        }

    private fun validateNotSelfReport(
        reporterId: String,
        targetId: String,
    ) {
        if (targetId == reporterId) {
            throw ReportException(ErrorStatus.CANNOT_REPORT_SELF)
        }
    }

    override fun createReport(user: User, request: UserReportServiceRequest): UserReport =
        UserReport(request.targetId, user, request.contentType)
}
