package nmnb.application.global.auth.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import nmnb.application.global.auth.exception.AuthException
import nmnb.application.global.auth.service.dto.KakaoProfile
import nmnb.application.global.auth.service.dto.OAuthToken
import nmnb.common.response.status.ErrorStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class KakaoUtil(
    @Value("\${kakao.auth.client}")
    private val client: String,
    @Value("\${kakao.auth.redirect}")
    private val redirect: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = ObjectMapper()
    private val restTemplate = RestTemplate()

    fun requestToken(accessCode: String): OAuthToken {
        log.info("Requesting OAuth token with code: {}", accessCode)

        val headers = defaultHeaders()
        val params = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", client)
            add("redirect_uri", redirect)
            add("code", accessCode)
        }

        val request = HttpEntity(params, headers)
        val response = restTemplate.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            request,
            String::class.java,
        )

        return parseBody(response.body, OAuthToken::class.java, "OAuthToken")
    }

    fun requestProfile(oAuthToken: OAuthToken): KakaoProfile {
        log.info("Requesting Kakao profile with accessToken: {}", oAuthToken.accessToken)

        val headers = defaultHeaders().apply {
            setBearerAuth(oAuthToken.accessToken)
        }

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val response = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            request,
            String::class.java,
        )

        log.debug("Kakao profile raw response: {}", response.body)

        return parseBody(response.body, KakaoProfile::class.java, "KakaoProfile")
    }

    private fun defaultHeaders(): HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_FORM_URLENCODED
    }

    private fun <T> parseBody(body: String?, clazz: Class<T>, typeName: String): T {
        return try {
            val parsed = objectMapper.readValue(body, clazz)
            log.info("Parsed $typeName: $parsed")
            parsed
        } catch (e: JsonProcessingException) {
            log.error("Failed to parse $typeName response: {}", body)
            throw AuthException(ErrorStatus.KAKAO_RESPONSE_PARSING_ERROR)
        }
    }
}
