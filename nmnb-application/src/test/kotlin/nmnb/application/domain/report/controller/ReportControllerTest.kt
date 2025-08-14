package nmnb.application.domain.report.controller

import nmnb.application.ControllerTestSupport
import nmnb.application.domain.report.controller.request.ReportRequest
import nmnb.application.domain.report.service.ReportService
import nmnb.application.domain.report.service.dto.request.PostReportServiceRequest
import nmnb.application.domain.report.service.dto.request.UserReportServiceRequest
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.report.ContentType
import nmnb.domain.report.PostReport
import nmnb.domain.report.ReportType
import nmnb.domain.report.UserReport
import nmnb.domain.user.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class ReportControllerTest : ControllerTestSupport() {

    @WithMockUser
    @DisplayName("신고 요청에 성공한다")
    @ParameterizedTest(name = "{index} : reportType = {0}")
    @EnumSource(ReportType::class)
    fun report(reportType: ReportType) {
        // given
        val deviceId = "deviceId"
        val accessToken = "access.token"
        val user = User.fixture()
        mockUserAuthentication(user, accessToken, deviceId)

        val request = ReportRequest(reportType, "1", ContentType.SEXUAL)
        mockReportService(reportType)

        // when & then
        mockMvc.perform(
            post("/v1/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Access-Token", accessToken)
                .header("Device-Id", deviceId)
                .with(csrf())
                .content(
                    objectMapper.writeValueAsString(request),
                ),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(SuccessStatus.OK.code))
    }

    private fun mockReportService(reportType: ReportType) {
        when (reportType) {
            ReportType.POST -> doNothing().whenever(reportService as ReportService<PostReport, PostReportServiceRequest>)
                .report(any(), any<PostReportServiceRequest>())

            ReportType.USER -> doNothing().whenever(reportService as ReportService<UserReport, UserReportServiceRequest>)
                .report(any(), any<UserReportServiceRequest>())
        }
    }
}
