package nmnb.application.global.auth.converter

import nmnb.common.response.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.auth.SocialType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToSocialTypeConverter : Converter<String, SocialType> {
    override fun convert(source: String): SocialType {
        return SocialType.values().find { it.value.equals(source, ignoreCase = true) }
            ?: throw AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE)
    }
}
