package nmnb.application.global.config

import nmnb.application.global.auth.converter.StringToSocialTypeConverter
import nmnb.application.global.auth.generator.AuthUserArgumentResolver
import nmnb.application.global.auth.generator.ExtractTokenArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val extractTokenArgumentResolver: ExtractTokenArgumentResolver,
    private val authUserArgumentResolver: AuthUserArgumentResolver,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(6000)
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToSocialTypeConverter())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(extractTokenArgumentResolver)
        resolvers.add(authUserArgumentResolver)
    }
}
