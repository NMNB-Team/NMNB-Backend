package nmnb.webflux

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["nmnb.common", "nmnb.r2dbc", "nmnb.webflux"])
class TestApplication
