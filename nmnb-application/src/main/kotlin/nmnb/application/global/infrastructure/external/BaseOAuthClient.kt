package nmnb.application.global.infrastructure.external

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import nmnb.application.global.auth.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

abstract class BaseOAuthClient(
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate,
) : OAuthClient {

    protected fun defaultHeaders(): HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_FORM_URLENCODED
    }

    protected fun <T> parseBody(body: String?, clazz: Class<T>, typeName: String): T {
        return try {
            val parsed = objectMapper.readValue(body, clazz)
            LoggerFactory.getLogger(javaClass).info("Parsed $typeName: $parsed")
            parsed
        } catch (e: JsonProcessingException) {
            LoggerFactory.getLogger(javaClass).error("Failed to parse $typeName response: {}", body)
            throw AuthException(ErrorStatus.OAUTH_RESPONSE_PARSING_ERROR)
        }
    }

    protected fun requestGet(accessToken: String, url: String): String {
        val headers = defaultHeaders().apply {
            setBearerAuth(accessToken)
        }
        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            String::class.java,
        )
        LoggerFactory.getLogger(javaClass).debug("Raw response from $url: ${response.body}")
        return response.body ?: ""
    }
}
