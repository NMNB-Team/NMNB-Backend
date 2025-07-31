package nmnb.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun nmnbAPI(): OpenAPI {
        val info = Info()
            .title("NMNB API")
            .description("내멍냥봐 API 명세서")
            .version("1.0.0")

        val accessTokenSchemeName = "AccessToken"
        val refreshTokenSchemeName = "RefreshToken"
        val deviceIdSchemeName = "DeviceId"

        val securityRequirement = SecurityRequirement()
            .addList(accessTokenSchemeName)
            .addList(refreshTokenSchemeName)
            .addList(deviceIdSchemeName)

        val components = Components()
            .addSecuritySchemes(
                accessTokenSchemeName,
                SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .`in`(SecurityScheme.In.HEADER)
                    .name("X-Access-Token"),
            )
            .addSecuritySchemes(
                refreshTokenSchemeName,
                SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .`in`(SecurityScheme.In.HEADER)
                    .name("X-Refresh-Token"),
            )
            .addSecuritySchemes(
                deviceIdSchemeName,
                SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .`in`(SecurityScheme.In.HEADER)
                    .name("Device-Id"),
            )

        return OpenAPI()
            .addServersItem(Server().url("/"))
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(components)
    }
}
