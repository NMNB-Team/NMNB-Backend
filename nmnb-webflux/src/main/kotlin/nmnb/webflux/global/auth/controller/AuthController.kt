package nmnb.webflux.global.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.common.handler.annotation.ExtractDeviceId
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.webflux.global.auth.controller.dto.request.AppleLoginRequest
import nmnb.webflux.global.auth.service.AuthService
import nmnb.webflux.global.auth.service.dto.response.AuthUserResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RequestMapping("/netty/v1/api/auth")
class AuthController(
    val authService: AuthService,
) {
    @Operation(summary = "Apple 소셜 로그인 API", description = "Apple 로그인 또는 회원가입을 수행합니다._예림")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
        ApiResponse(responseCode = "AUTH413", description = "애플 ID 토큰에 이메일 정보가 없습니다."),
        ApiResponse(responseCode = "AUTH414", description = "애플 ID 토큰의 발행자가 유효하지 않습니다."),
        ApiResponse(responseCode = "AUTH415", description = "애플 ID 토큰의 수신자가 유효하지 않습니다."),
    )
    @PostMapping("/login/apple")
    fun signInWithApple(
        @RequestBody request: AppleLoginRequest,
        @Parameter(hidden = true) @ExtractDeviceId deviceId: String,
    ): Mono<BaseResponse<AuthUserResponse>> {
        return authService.appleLogin(request.toAppleLoginServiceRequest(), deviceId)
            .map { result ->
                BaseResponse.onSuccess(SuccessStatus.OK, result)
            }
    }
}
