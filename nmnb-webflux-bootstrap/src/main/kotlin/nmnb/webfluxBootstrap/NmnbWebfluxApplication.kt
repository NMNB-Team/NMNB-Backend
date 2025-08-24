package nmnb.webfluxBootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@EnableRedisRepositories(basePackages = ["nmnb.common.auth.repository"])
@SpringBootApplication(scanBasePackages = ["nmnb.r2dbc", "nmnb.webflux", "nmnb.common"])
class NmnbWebfluxApplication

fun main(args: Array<String>) {
    runApplication<NmnbWebfluxApplication>(*args)
}
