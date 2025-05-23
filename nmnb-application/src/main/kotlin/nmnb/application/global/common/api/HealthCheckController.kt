package nmnb.application.global.common.api

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "X_HealthCheck", description = "서버 헬스 체크 API")
class HealthCheckController {
    @GetMapping("/health")
    fun healthCheck(): String {
        return "나는 건강합니다 💪"
    }
}
