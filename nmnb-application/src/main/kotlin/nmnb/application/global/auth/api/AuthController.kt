package nmnb.application.global.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.global.auth.service.AuthService
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RequestMapping("/v1/api/auth")
class AuthController(
    val authService: AuthService,
) {
    @Operation(summary = "카카오 로그인 API", description = "카카오 인가 코드를 이용해 로그인 또는 회원가입을 수행합니다._예림")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
        ApiResponse(responseCode = "AUTH500", description = "카카오 서버 응답을 처리하는 중 오류가 발생했습니다."),
    )
    @PostMapping("/login/kakao")
    fun signIn(@RequestParam("code") accessCode: String): BaseResponse<AuthUserResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, authService.signInWithSocial(accessCode))
    }
}
