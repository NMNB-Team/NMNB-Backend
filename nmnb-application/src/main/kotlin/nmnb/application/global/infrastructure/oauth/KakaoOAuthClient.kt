package nmnb.application.global.infrastructure.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import nmnb.application.global.auth.service.dto.KakaoProfile
import nmnb.application.global.auth.service.dto.OAuthProfile
import nmnb.domain.auth.SocialType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class KakaoOAuthClient(
    objectMapper: ObjectMapper,
    restTemplate: RestTemplate,
) : BaseOAuthClient(objectMapper, restTemplate) {

    override fun requestProfile(accessCode: String): OAuthProfile {
        val body = requestGet(accessCode, KAKAO_PROFILE_URL)
        return parseBody(body, KakaoProfile::class.java, "KakaoProfile")
    }

    override fun supports(type: SocialType): Boolean = type == SocialType.KAKAO

    companion object {
        private const val KAKAO_PROFILE_URL = "https://kapi.kakao.com/v2/user/me"
    }
}
