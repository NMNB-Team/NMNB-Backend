package nmnb.application.global.config

import nmnb.application.global.auth.converter.StringToSocialTypeConverter
import nmnb.application.global.auth.generator.AuthUserArgumentResolver
import nmnb.application.global.auth.generator.ExtractAccessTokenArgumentResolver
import nmnb.application.global.auth.generator.ExtractDeviceIdArgumentResolver
import nmnb.application.global.auth.generator.ExtractRefreshTokenArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val extractRefreshTokenArgumentResolver: ExtractRefreshTokenArgumentResolver,
    private val extractAccessTokenArgumentResolver: ExtractAccessTokenArgumentResolver,
    private val authUserArgumentResolver: AuthUserArgumentResolver,
    private val extractDeviceIdArgumentResolver: ExtractDeviceIdArgumentResolver,
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
        resolvers.add(extractRefreshTokenArgumentResolver)
        resolvers.add(extractAccessTokenArgumentResolver)
        resolvers.add(authUserArgumentResolver)
        resolvers.add(extractDeviceIdArgumentResolver)
    }
}
