package cv.nmnb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NmnbBackApplication

fun main(args: Array<String>) {
    runApplication<NmnbBackApplication>(*args)
}
