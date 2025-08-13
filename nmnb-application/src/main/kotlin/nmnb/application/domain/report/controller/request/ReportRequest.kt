package nmnb.application.domain.report.controller.request

import nmnb.domain.report.ContentType
import nmnb.domain.report.ReportType

data class ReportRequest(
    val type: ReportType,
    val targetId: String,
    val contentType: ContentType,
)
