package nmnb.application

import com.fasterxml.jackson.databind.ObjectMapper
import nmnb.application.domain.block.controller.BlockController
import nmnb.application.domain.block.service.UserBlockService
import nmnb.application.domain.like.controller.LikeController
import nmnb.application.domain.like.service.LikeService
import nmnb.application.domain.post.controller.PostController
import nmnb.application.domain.post.service.PostService
import nmnb.application.domain.report.controller.ReportController
import nmnb.application.domain.report.service.PostReportService
import nmnb.application.domain.report.service.ReportService
import nmnb.application.domain.report.service.UserReportService
import nmnb.application.domain.user.controller.UserController
import nmnb.application.domain.user.service.UserService
import nmnb.application.global.auth.controller.AuthController
import nmnb.application.global.auth.generator.AuthUserArgumentResolver
import nmnb.application.global.auth.generator.ExtractAccessTokenArgumentResolver
import nmnb.application.global.auth.generator.ExtractDeviceIdArgumentResolver
import nmnb.application.global.auth.generator.ExtractRefreshTokenArgumentResolver
import nmnb.application.global.auth.service.AuthService
import nmnb.application.global.common.utils.ResponseUtils
import nmnb.application.global.config.SecurityConfig
import nmnb.application.global.infrastructure.security.BlacklistService
import nmnb.application.global.infrastructure.security.JwtProvider
import nmnb.common.utils.JwtConstants
import nmnb.domain.user.User
import nmnb.domain.user.repository.UserRepository
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(
    controllers = [
        UserController::class,
        LikeController::class,
        PostController::class,
        AuthController::class,
        ReportController::class,
        BlockController::class,
    ],
)
@Import(
    SecurityConfig::class,
    AuthUserArgumentResolver::class,
    ExtractRefreshTokenArgumentResolver::class,
    ExtractAccessTokenArgumentResolver::class,
    ExtractDeviceIdArgumentResolver::class,
)
@ContextConfiguration(classes = [TestApplication::class])
abstract class ControllerTestSupport {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockBean
    protected lateinit var userService: UserService

    @MockBean
    protected lateinit var likeService: LikeService

    @MockBean
    protected lateinit var postService: PostService

    @MockBean
    protected lateinit var authService: AuthService

    @MockBean
    protected lateinit var reportService: ReportService<*, *>

    @MockBean
    lateinit var postReportService: PostReportService

    @MockBean
    lateinit var userReportService: UserReportService

    @MockBean
    lateinit var userBlockService: UserBlockService

    @MockBean
    protected lateinit var userRepository: UserRepository

    @MockBean
    lateinit var jwtProvider: JwtProvider

    @MockBean
    protected lateinit var blacklistService: BlacklistService

    @MockBean
    protected lateinit var responseUtils: ResponseUtils

    fun mockUserAuthentication(user: User, accessToken: String, deviceId: String) {
        whenever(jwtProvider.getEmailWithValidation(accessToken)).thenReturn(user.email)
        whenever(userRepository.findByEmail(user.email)).thenReturn(user)
        whenever(jwtProvider.getClaimFromToken(accessToken, JwtConstants.DEVICE_ID_CLAIM_KEY)).thenReturn(deviceId)
    }
}
