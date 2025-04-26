package nmnb.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["nmnb"])
class NmnbBackApplication

fun main(args: Array<String>) {
    runApplication<NmnbBackApplication>(*args)
}
