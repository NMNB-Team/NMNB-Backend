package nmnb.domain.auth

import nmnb.application.global.auth.exception.AuthException
import nmnb.common.response.status.ErrorStatus

enum class SocialType(val value: String) {
    KAKAO("kakao"),
    ;

    companion object {
        fun from(value: String): SocialType =
            entries.find { it.value.equals(value, ignoreCase = true) }
                ?: throw AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE)
    }
}
