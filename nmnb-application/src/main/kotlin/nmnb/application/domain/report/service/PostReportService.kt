package nmnb.application.domain.report.service

import nmnb.application.domain.report.service.dto.request.PostReportServiceRequest
import nmnb.domain.report.PostReport
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostReportService(
    private val reportRepository: ReportRepository,
) : ReportService(reportRepository) {
    @Transactional
    fun report(user: User, request: PostReportServiceRequest) {
        val report = PostReport(request.targetId, user.id!!, request.contentType)
        save(report)
    }
}
