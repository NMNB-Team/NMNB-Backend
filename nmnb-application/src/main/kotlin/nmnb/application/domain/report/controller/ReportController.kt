package nmnb.application.domain.report.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.report.controller.request.ReportRequest
import nmnb.application.domain.report.service.PostReportService
import nmnb.application.domain.report.service.UserReportService
import nmnb.common.handler.annotation.AuthUser
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.report.ReportType
import nmnb.domain.user.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Report 🙈", description = "신고 및 차단 관련 API")
@RequestMapping("/v1/api")
class ReportController(
    private val postReportService: PostReportService,
    private val userReportService: UserReportService,
) {
    @Operation(
        summary = "신고 API",
        description = """
            신고 타입(type)과 신고 유형(contentType)에 따라 신고 처리합니다.
            신고 대상 아이디 값들은 모두 String으로 전달합니다._숙희
        """,
    )
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
        ApiResponse(responseCode = "REPORT401", description = "유효한 Post 아이디가 아닙니다."),
        ApiResponse(responseCode = "REPORT402", description = "본인을 신고할 수 없습니다."),
        ApiResponse(responseCode = "REPORT403", description = "이미 신고한 적이 있습니다."),
    )
    @PostMapping("/reports")
    fun postReport(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
        @RequestBody request: ReportRequest,
    ): BaseResponse<Any> {
        val result = when (request.type) {
            ReportType.POST -> postReportService.report(user, request.toPostServiceRequest())
            ReportType.USER -> userReportService.report(user, request.toUserServiceRequest())
        }
        return BaseResponse.onSuccess(SuccessStatus.OK, result)
    }
}
