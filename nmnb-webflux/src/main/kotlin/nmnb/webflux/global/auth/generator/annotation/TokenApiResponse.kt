package nmnb.webflux.global.auth.generator.annotation

import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@ApiResponses(
    ApiResponse(responseCode = "AUTH402", description = "잘못된 토큰 정보입니다."),
    ApiResponse(responseCode = "AUTH403", description = "만료된 토큰입니다."),
    ApiResponse(responseCode = "AUTH404", description = "지원하지 않는 토큰입니다."),
    ApiResponse(responseCode = "AUTH405", description = "토큰이 요청에 포함되어 있지 않습니다."),
    ApiResponse(responseCode = "AUTH406", description = "빈 토큰이 전달되었습니다."),
    ApiResponse(responseCode = "AUTH407", description = "토큰에 이메일 정보가 없습니다."),
)
annotation class TokenApiResponse