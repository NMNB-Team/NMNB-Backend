package nmnb.application.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()

        val messageConverters: MutableList<HttpMessageConverter<*>> = ArrayList()
        messageConverters.add(FormHttpMessageConverter())
        messageConverters.add(StringHttpMessageConverter())
        restTemplate.messageConverters = messageConverters

        return restTemplate
    }
}
