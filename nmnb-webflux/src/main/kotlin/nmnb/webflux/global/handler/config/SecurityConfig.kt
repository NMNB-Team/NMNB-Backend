package nmnb.webflux.global.handler.config

import nmnb.webflux.global.handler.auth.utils.JWTFilter
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
) {

    private val allowedUrl = arrayOf(
        "/**",
        // "/webjars/swagger-ui/**",
        // "/v3/api-docs/**",
    )

    private val userUrl = arrayOf(
        "/v1/api/upload",
    )

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf { it.disable() }
        http.httpBasic { httpBasicConfigurer -> httpBasicConfigurer.disable() }

        http.formLogin { formLoginConfigurer -> formLoginConfigurer.disable() }

        http
            .securityContextRepository(WebSessionServerSecurityContextRepository())

        http.authorizeExchange {
            it.pathMatchers(*allowedUrl).permitAll()
                .pathMatchers(*userUrl)
                .hasRole("USER")
                .anyExchange().authenticated()
        }

        http.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)

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
