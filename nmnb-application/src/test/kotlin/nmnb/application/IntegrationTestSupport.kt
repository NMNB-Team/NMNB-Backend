package nmnb.application

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("local")
@SpringBootTest(classes = [TestApplication::class])
abstract class IntegrationTestSupport
