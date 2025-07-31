package nmnb.webflux.global.config

import nmnb.webflux.global.infrastructure.security.DeviceValidationFilter
import nmnb.webflux.global.infrastructure.security.JWTFilter
import nmnb.webflux.global.utils.SecurityConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.web.server.WebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtFilter: JWTFilter,
    private val deviceValidationFilter: DeviceValidationFilter,
) {
    private val userUrl = arrayOf(
        "/netty/v1/api/upload",
    )

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf { it.disable() }
        http.httpBasic { httpBasicConfigurer -> httpBasicConfigurer.disable() }

        http.formLogin { formLoginConfigurer -> formLoginConfigurer.disable() }

        http
            .securityContextRepository(WebSessionServerSecurityContextRepository())

        http.authorizeExchange {
            it.pathMatchers(*SecurityConstants.SWAGGER_PATHS.map { path -> "$path/**" }.toTypedArray()).permitAll()
                .pathMatchers(*userUrl)
                .hasRole("USER")
                .anyExchange().authenticated()
        }

        http.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        http.addFilterAt(deviceValidationFilter, SecurityWebFiltersOrder.AUTHORIZATION)

        return http.build()
    }

    @Bean
    fun headersWebFilter(): WebFilter {
        return WebFilter { exchange, chain ->
            exchange.response.headers.add("X-Frame-Options", "SAMEORIGIN")
            chain.filter(exchange)
        }
    }
}
