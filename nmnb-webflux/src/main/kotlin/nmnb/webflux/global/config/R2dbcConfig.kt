package nmnb.webflux.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = ["nmnb.r2dbc", "nmnb.webflux.auth"])
class R2dbcConfig
