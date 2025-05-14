package nmnb.webflux.global.handler.common.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {
    @GetMapping("/webjars/health")
    fun healthCheck(): String {
        return "나는 건강합니다 (Webflux) 💪"
    }
}
