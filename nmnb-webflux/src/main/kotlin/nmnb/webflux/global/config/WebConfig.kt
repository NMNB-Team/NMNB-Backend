package nmnb.webflux.global.config

import nmnb.webflux.global.handler.resolver.AuthUserArgumentResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebConfig(
    private val authUserArgumentResolver: AuthUserArgumentResolver,
) : WebFluxConfigurer {

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val config = CorsConfiguration().apply {
            addAllowedOrigin("*")
            addAllowedMethod("*")
            addAllowedHeader("*")
            allowCredentials = false
            maxAge = 6000
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }

        return CorsWebFilter(source)
    }

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(authUserArgumentResolver)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/netty/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }
}
