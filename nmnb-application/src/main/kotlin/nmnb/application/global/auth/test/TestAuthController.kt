package nmnb.application.global.auth.test

import io.swagger.v3.oas.annotations.tags.Tag
import nmnb.application.global.auth.test.dto.TestAuthResponse
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.status.SuccessStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Tag(name = "Auth🔒Test", description = "인증/인가 테스트 용")
class TestAuthController(
    private val testAuthService: TestAuthService,
) {
    @PostMapping("/login")
    fun login(@RequestParam(value = "email") email: String): BaseResponse<TestAuthResponse> {
        return BaseResponse.onSuccess(SuccessStatus.NO_CONTENT, testAuthService.login(email))
    }
}
