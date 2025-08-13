package nmnb.application.domain.report.service.dto.request

import nmnb.domain.report.ContentType

data class UserReportServiceRequest(
    val targetId: String,
    val contentType: ContentType,
)
