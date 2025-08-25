package nmnb.application.domain.report.controller.request

import nmnb.application.domain.report.service.dto.request.PostReportServiceRequest
import nmnb.application.domain.report.service.dto.request.UserReportServiceRequest
import nmnb.common.response.exception.ReportException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.report.ContentType
import nmnb.domain.report.ReportType

data class ReportRequest(
    val type: ReportType,
    val targetId: String,
    val contentType: ContentType,
) {
    fun toPostServiceRequest(): PostReportServiceRequest {
        val id = targetId.toLongOrNull() ?: throw ReportException(ErrorStatus.INVALID_POST_TARGET_ID)
        return PostReportServiceRequest(id, contentType)
    }

    fun toUserServiceRequest(): UserReportServiceRequest {
        return UserReportServiceRequest(targetId, contentType)
    }
}
