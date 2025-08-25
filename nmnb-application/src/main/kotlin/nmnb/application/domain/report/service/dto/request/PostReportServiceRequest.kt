package nmnb.application.domain.report.service.dto.request

import nmnb.domain.report.ContentType

data class PostReportServiceRequest(
    val targetId: Long,
    val contentType: ContentType,
)
