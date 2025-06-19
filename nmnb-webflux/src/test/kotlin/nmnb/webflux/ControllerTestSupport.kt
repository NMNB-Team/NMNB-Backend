package nmnb.webflux

import nmnb.r2dbc.user.R2dbcUserRepository
import nmnb.webflux.domain.post.controller.PostController
import nmnb.webflux.domain.post.service.PostUploadService
import nmnb.webflux.global.config.SecurityConfig
import nmnb.webflux.global.handler.resolver.AuthUserArgumentResolver
import nmnb.webflux.global.infrastructure.security.BlacklistService
import nmnb.webflux.global.infrastructure.security.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [PostController::class])
@Import(SecurityConfig::class, AuthUserArgumentResolver::class)
abstract class ControllerTestSupport {
    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var context: ApplicationContext

    @MockBean
    protected lateinit var jwtProvider: JwtProvider

    @MockBean
    protected lateinit var userRepository: R2dbcUserRepository

    @MockBean
    protected lateinit var postUploadService: PostUploadService

    @MockBean
    protected lateinit var blacklistService: BlacklistService
}
