package nmnb.application.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    val allowedUrl: Array<String> = arrayOf(
        "/**",
        "/health",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v1/api/auth/**",
        "/v1/api/users/pet",
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
                .anyRequest().authenticated()
        }

        return http.build()
    }
}
