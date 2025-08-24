package nmnb.webflux.global.config

import nmnb.webflux.global.properties.AppleProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
class AppleConfig(
    private val appleProperties: AppleProperties,
) {
    @Bean
    fun appleJwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(appleProperties.jwtSetUrl).build()
    }
}
