package nmnb.application.domain.block.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.block.controller.dto.request.UserBlockRequest
import nmnb.application.domain.block.service.UserBlockService
import nmnb.common.handler.annotation.AuthUser
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Block 🔒", description = "차단 관련 API")
@RequestMapping("/v1/api/blocks")
class BlockController(
    private val userBlockService: UserBlockService,
) {
    @Operation(
        summary = "사용자 차단 API",
        description = "사용자를 차단합니다. 차단한 사용자는 페이지에 조회되지 않습니다._숙희",
    )
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @PostMapping("")
    fun blockUser(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
        @RequestBody request: UserBlockRequest,
    ): BaseResponse<Any> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            userBlockService.block(user, request.toUserBlockServiceRequest()),
        )
    }
}
