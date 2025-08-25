package nmnb.application.domain.report.service

import nmnb.application.domain.report.service.dto.request.PostReportServiceRequest
import nmnb.common.response.exception.PostException
import nmnb.common.response.exception.ReportException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.post.Post
import nmnb.domain.post.repository.PostRepository
import nmnb.domain.report.PostReport
import nmnb.domain.report.repository.ReportRepository
import nmnb.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostReportService(
    private val reportRepository: ReportRepository,
    private val postRepository: PostRepository,
) : ReportService<PostReport, PostReportServiceRequest>(reportRepository) {
    override fun validateReport(user: User, request: PostReportServiceRequest) {
        val post = fetchPost(request.targetId)
        validateNotSelfReport(user.id!!, post)
    }

    private fun fetchPost(targetId: Long): Post {
        return postRepository.findById(targetId).orElseThrow {
            PostException(ErrorStatus.POST_NOTFOUND)
        }
    }

    private fun validateNotSelfReport(
        reporterId: String,
        post: Post,
    ) {
        if (post.user.id == reporterId) {
            throw ReportException(ErrorStatus.CANNOT_REPORT_SELF)
        }
    }

    override fun createReport(user: User, request: PostReportServiceRequest): PostReport {
        return PostReport(request.targetId, user, request.contentType)
    }
}
