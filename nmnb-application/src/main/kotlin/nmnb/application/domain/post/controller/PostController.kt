package nmnb.application.domain.post.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.domain.post.service.PostService
import nmnb.application.domain.post.service.dto.request.PostPageServiceRequest
import nmnb.application.domain.post.service.dto.response.PostPageResponse
import nmnb.application.global.auth.generator.annotation.TokenApiResponse
import nmnb.common.handler.annotation.AuthUser
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.user.User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Post 🎞️", description = "영상 관련 API")
@RequestMapping("/v1/api")
class PostController(
    private val postService: PostService,
) {
    @Operation(
        summary = "게시글 랜덤 조회 API",
        description = "메인화면의 영상을 조회합니다. " +
            "기본 7개가 조회되며, 마지막으로 조회된 cursor값을 request로 받습니다." +
            "`seed` 값은 메인 화면에 처음 접근할 때 클라이언트가 랜덤으로 생성하여 사용해야 합니다. " +
            "이후 이어지는 요청에서는 처음 생성한 동일한 `seed`값을 계속 사용하여, 랜덤 순서가 고정된 상태로 페이징 조회를 이어갈 수 있습니다._숙희",
    )
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @TokenApiResponse
    @GetMapping("/videos")
    fun getPostPage(
        @RequestParam seed: Int,
        @RequestParam(required = false, defaultValue = "-1") cursor: Int,
        @RequestParam(required = false, defaultValue = "7") size: Int,
        @Parameter(name = "user", hidden = true) @AuthUser(required = false) user: User?,
    ): BaseResponse<PostPageResponse> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            postService.getPostPage(user?.id, PostPageServiceRequest(seed, cursor, size)),
        )
    }

    @Operation(
        summary = "게시글 삭제 API",
        description = "본인이 작성한 게시글을 삭제합니다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "COMMON202", description = "요청 성공 및 반환할 콘텐츠가 없음"),
        ApiResponse(responseCode = "POST400", description = "게시물을 찾을 수 없습니다."),
        ApiResponse(responseCode = "POST401", description = "본인이 작성한 게시글이 아닙니다."),
    )
    @DeleteMapping("/videos/{postId}")
    fun deletePost(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
        @PathVariable(name = "postId") postId: Long,
    ): BaseResponse<Any> {
        return BaseResponse.onSuccess(
            SuccessStatus.NO_CONTENT,
            postService.deletePost(user, postId),
        )
    }
}
