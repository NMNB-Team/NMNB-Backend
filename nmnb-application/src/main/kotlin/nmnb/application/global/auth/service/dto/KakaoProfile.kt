package nmnb.application.global.auth.service.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoProfile(
    val id: Long = 0L,
    val connectedAt: String = "",
    val kakaoAccount: KakaoAccount = KakaoAccount(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoAccount(
    val hasEmail: Boolean = false,
    val emailNeedsAgreement: Boolean = false,
    val isEmailValid: Boolean = false,
    val isEmailVerified: Boolean = false,
    val email: String = "",
)
