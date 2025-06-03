package nmnb.application.global.config

import nmnb.application.global.infrastructure.security.JWTFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JWTFilter,
) {

    val allowedUrl: Array<String> = arrayOf(
        "/health",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/api/login",
        "/v1/api/auth/**",
    )
    private val userUrl = arrayOf(
        "/v1/api/users/pet",
        "/v1/api/users/profile",
    )

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrfConfigurer -> csrfConfigurer.disable() }

        http.httpBasic { httpBasicConfigurer -> httpBasicConfigurer.disable() }

        http.formLogin { formLoginConfigurer -> formLoginConfigurer.disable() }

        http.sessionManagement { sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.headers { headers ->
            headers.frameOptions { frameOptionsConfig ->
                frameOptionsConfig.sameOrigin()
            }
        }

        http.authorizeHttpRequests {
            it.requestMatchers(*allowedUrl).permitAll()
                .requestMatchers(*userUrl).hasRole("USER")
                .anyRequest().authenticated()
        }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
