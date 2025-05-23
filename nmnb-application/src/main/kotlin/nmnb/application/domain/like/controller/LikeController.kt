package nmnb.application.domain.like.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.like.controller.dto.request.PostLikeRequest
import nmnb.application.domain.like.service.LikeService
import nmnb.application.global.auth.generator.annotation.AuthUser
import nmnb.application.global.auth.generator.annotation.TokenApiResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import org.apache.catalina.User
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Like 🫰", description = "좋아요 관련 API")
@RequestMapping("/v1/api")
class LikeController(
    private val likeService: LikeService,
) {
    @Operation(summary = "좋아요 등록/취소 API", description = "좋아요가 등록되어있으면 취소, 좋아요가 없으면 등록합니다._숙희")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @TokenApiResponse
    @PatchMapping("/like")
    fun likeOrUnlike(@Parameter(name = "user", hidden = true) @AuthUser user: User, @RequestBody request: PostLikeRequest): BaseResponse<Any> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            likeService.likeOrUnlike(request.user, request.toServiceRequest()),
        )
    }
}
