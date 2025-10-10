package nmnb.application.domain.badge.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.badge.controller.dto.response.BadgeInfoListResponse
import nmnb.application.domain.badge.service.BadgeService
import nmnb.common.handler.annotation.AuthUser
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Badge 🎞️", description = "뱃지 관련 API")
@RequestMapping("/v1/api")
class BadgeController(
    private val badgeService: BadgeService,
) {

    @Operation(summary = "뱃지 조회 API", description = "사용자가 구매 가능한 모든 뱃지 목록과 구매 여부를 조회합니다._숙희")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @GetMapping("/badge")
    fun getBadges(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
    ): BaseResponse<BadgeInfoListResponse> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            badgeService.getBadges(user.id!!),
        )
    }
}
