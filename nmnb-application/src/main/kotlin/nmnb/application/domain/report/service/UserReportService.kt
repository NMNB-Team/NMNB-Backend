package nmnb.application.domain.report.service

import nmnb.application.domain.report.service.dto.request.UserReportServiceRequest
import nmnb.common.response.exception.ReportException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.report.UserReport
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserReportService(
    reportRepository: ReportRepository,
) : ReportService<UserReport, UserReportServiceRequest>(reportRepository) {
    override fun validateReport(user: User, request: UserReportServiceRequest) {
        validateNotSelfReport(user.id!!, request.targetId)
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
        UserReport(request.targetId, user.id!!, request.contentType)
}
