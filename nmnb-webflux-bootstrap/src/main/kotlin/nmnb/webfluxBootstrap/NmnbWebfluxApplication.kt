package nmnb.webfluxBootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["nmnb.r2dbc", "nmnb.webflux", "nmnb.common"])
class NmnbWebfluxApplication

fun main(args: Array<String>) {
    runApplication<NmnbWebfluxApplication>(*args)
}
