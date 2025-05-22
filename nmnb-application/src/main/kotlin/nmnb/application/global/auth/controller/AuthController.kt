package nmnb.application.global.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.global.auth.generator.annotation.ExtractToken
import nmnb.application.global.auth.generator.annotation.TokenApiResponse
import nmnb.application.global.auth.service.AuthService
import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.auth.SocialType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Auth 🔐", description = "인증 관련 API")
@RequestMapping("/v1/api/auth")
class AuthController(
    val authService: AuthService,
) {
    @Operation(summary = "소셜 로그인 API", description = "SocialType(KAKAO, GOOGLE, NAVER 등)을 받아 로그인 또는 회원가입을 수행합니다._예림")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
        ApiResponse(responseCode = "AUTH400", description = "지원하지 않는 소셜 로그인 타입입니다."),
        ApiResponse(responseCode = "AUTH500", description = "OAuth 서버 응답을 처리하는 중 오류가 발생했습니다."),
    )
    @GetMapping("/login/{type}")
    fun signIn(@RequestParam("code") accessCode: String, @PathVariable("type") type: SocialType): BaseResponse<AuthUserResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, authService.signInWithSocial(accessCode, type))
    }

    @Operation(summary = "토큰 재발급 API", description = "토큰을 재발급합니다_예림")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @TokenApiResponse
    @GetMapping("/refresh")
    fun refreshToken(@ExtractToken refreshToken: String): BaseResponse<AuthTokenResponse> {
        return BaseResponse.onSuccess(SuccessStatus.OK, authService.refreshToken(refreshToken))
    }
}
