package nmnb.webflux.domain.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.r2dbc.user.R2dbcUser
import nmnb.webflux.domain.post.controller.dto.request.PostInfoRequest
import nmnb.webflux.domain.post.service.PostUploadService
import nmnb.webflux.global.auth.generator.annotation.TokenApiResponse
import nmnb.webflux.global.handler.annotation.AuthUser
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Post 🎞️", description = "영상 관련 API")
@RequestMapping("/v1/api")
class PostController(
    private val objectMapper: ObjectMapper,
    private val postUploadService: PostUploadService,
) {
    @Operation(summary = "영상 업로드 API", description = "영상을 업로드 합니다._숙희")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON202", description = "요청 성공 및 반환할 콘텐츠가 없음."),
    )
    @TokenApiResponse
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadPost(
        @Parameter(name = "user", hidden = true) @AuthUser user: R2dbcUser,
        @RequestPart(value = "file") file: FilePart,
        @RequestPart("request") request: String,
    ): BaseResponse<Any> {
        val postInfo = objectMapper.readValue(request, PostInfoRequest::class.java)

        return BaseResponse.onSuccess(
            SuccessStatus.NO_CONTENT,
            postUploadService.upload(user, file, postInfo.toServiceRequest()),
        )
    }
}
