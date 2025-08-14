package nmnb.application.domain.report.service

import nmnb.common.response.exception.ReportException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.report.Report
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional

abstract class ReportService<R : Report, Request>(
    private val reportRepository: ReportRepository,
) {
    fun save(report: R) {
        try {
            reportRepository.save(report)
        } catch (e: DataIntegrityViolationException) {
            throw ReportException(ErrorStatus.ALREADY_REPORTED)
        }
    }

    @Transactional
    open fun report(user: User, request: Request) {
        validateReport(user, request)
        val report = createReport(user, request)
        save(report)
    }

    abstract fun validateReport(user: User, request: Request)

    abstract fun createReport(user: User, request: Request): R
}
