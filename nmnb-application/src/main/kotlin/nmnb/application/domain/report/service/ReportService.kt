package nmnb.application.domain.report.service

import nmnb.domain.report.Report
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import org.springframework.transaction.annotation.Transactional

abstract class ReportService<R : Report, Request>(
    private val reportRepository: ReportRepository,
) {
    fun save(report: R) = reportRepository.save(report)

    @Transactional
    open fun report(user: User, request: Request) {
        val report = createReport(user, request)
        save(report)
    }

    abstract fun createReport(user: User, request: Request): R
}
