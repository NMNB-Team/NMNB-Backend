package nmnb.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["nmnb.common", "nmnb.domain", "nmnb.application"])
class NmnbBackApplication

fun main(args: Array<String>) {
    runApplication<NmnbBackApplication>(*args)
}
