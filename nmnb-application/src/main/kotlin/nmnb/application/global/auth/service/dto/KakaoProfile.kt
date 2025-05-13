package nmnb.application.global.auth.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoProfile(
    val kakaoAccount: KakaoAccount = KakaoAccount(),
) : OAuthProfile {
    override fun getEmail(): String = kakaoAccount.email
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoAccount(
    val email: String = "",
)
