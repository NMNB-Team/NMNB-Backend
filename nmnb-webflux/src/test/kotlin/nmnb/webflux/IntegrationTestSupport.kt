package nmnb.webflux

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [TestApplication::class])
abstract class IntegrationTestSupport
