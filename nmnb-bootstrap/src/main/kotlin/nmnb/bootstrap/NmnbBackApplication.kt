package nmnb.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@EnableRedisRepositories(basePackages = ["nmnb.common.auth.repository"])
@SpringBootApplication(scanBasePackages = ["nmnb.common", "nmnb.domain", "nmnb.application"])
class NmnbBackApplication

fun main(args: Array<String>) {
    runApplication<NmnbBackApplication>(*args)
}
