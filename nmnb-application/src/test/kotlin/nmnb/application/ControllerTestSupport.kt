package nmnb.application

import com.fasterxml.jackson.databind.ObjectMapper
import nmnb.application.domain.like.controller.LikeController
import nmnb.application.domain.like.service.LikeService
import nmnb.application.domain.post.controller.PostController
import nmnb.application.domain.post.service.PostService
import nmnb.application.domain.user.controller.UserController
import nmnb.application.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(
    controllers = [
        UserController::class,
        LikeController::class,
        PostController::class,
    ],
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
}
