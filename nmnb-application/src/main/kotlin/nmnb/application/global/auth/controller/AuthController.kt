package nmnb.application.global.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.global.auth.generator.annotation.ExtractAccessToken
import nmnb.application.global.auth.generator.annotation.ExtractRefreshToken
import nmnb.application.global.auth.generator.annotation.TokenApiResponse
import nmnb.application.global.auth.service.AuthService
import nmnb.application.global.auth.service.dto.response.AuthTokenResponse
import nmnb.application.global.auth.service.dto.response.AuthUserResponse
import nmnb.common.handler.annotation.AuthUser
import nmnb.common.handler.annotation.ExtractDeviceId
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import nmnb.domain.auth.SocialType
import nmnb.domain.user.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
    @Operation(summary = "소셜 로그인 API", description = "SocialType(KAKAO, GOOGLE, NAVER 등)을 받아 로그인 또는 회원가입을 수행합니다._예림")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
        ApiResponse(responseCode = "AUTH400", description = "지원하지 않는 소셜 로그인 타입입니다."),
        ApiResponse(responseCode = "AUTH500", description = "OAuth 서버 응답을 처리하는 중 오류가 발생했습니다."),
    )
    @GetMapping("/login/{type}")
    fun signIn(
        @RequestParam("code") accessCode: String,
        @PathVariable("type") type: SocialType,
        @Parameter(hidden = true) @ExtractDeviceId deviceId: String,
    ): BaseResponse<AuthUserResponse> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            authService.signInWithSocial(accessCode, type, deviceId),
        )
    }

    @Operation(summary = "토큰 재발급 API", description = "토큰을 재발급합니다_예림")
    @ApiResponses(ApiResponse(responseCode = "COMMON200", description = "성공입니다."))
    @TokenApiResponse
    @GetMapping("/refresh")
    fun refreshToken(
        @Parameter(hidden = true) @ExtractRefreshToken refreshToken: String,
        @Parameter(hidden = true) @ExtractDeviceId deviceId: String,
    ): BaseResponse<AuthTokenResponse> {
        return BaseResponse.onSuccess(
            SuccessStatus.OK,
            authService.refreshToken(refreshToken, deviceId),
        )
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃을 수행하고, 전달받은 AccessToken을 블랙리스트에 등록하여 해당 토큰의 재사용을 막습니다._숙희")
    @ApiResponses(
        ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
    )
    @TokenApiResponse
    @PostMapping("/logout")
    fun logout(
        @Parameter(name = "user", hidden = true) @AuthUser user: User,
        @Parameter(name = "deviceId", hidden = true) @ExtractDeviceId deviceId: String,
        @Parameter(name = "refreshToken", hidden = true) @ExtractRefreshToken refreshToken: String,
        @Parameter(name = "accessToken", hidden = true) @ExtractAccessToken accessToken: String,
    ): BaseResponse<Any> {
        return BaseResponse.onSuccess(SuccessStatus.OK, authService.logout(user, deviceId, accessToken, refreshToken))
    }
}
