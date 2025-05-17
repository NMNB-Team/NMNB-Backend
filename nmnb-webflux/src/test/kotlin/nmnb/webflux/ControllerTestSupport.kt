package nmnb.webflux

import nmnb.webflux.domain.post.controller.PostController
import nmnb.webflux.global.config.SecurityConfig
import nmnb.webflux.global.handler.resolver.AuthUserArgumentResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [PostController::class])
@Import(SecurityConfig::class, AuthUserArgumentResolver::class)
abstract class ControllerTestSupport {
    @Autowired
    protected lateinit var webTestClient: WebTestClient
}
