package nmnb.application.domain.report.service

import nmnb.domain.report.Report
import nmnb.domain.report.repository.ReportRepository

abstract class ReportService(
    private val reportRepository: ReportRepository,
) {
    fun save(report: Report) = reportRepository.save(report)
}
