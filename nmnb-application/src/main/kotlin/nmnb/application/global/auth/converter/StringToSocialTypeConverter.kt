package nmnb.application.global.auth.converter

import nmnb.domain.auth.SocialType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToSocialTypeConverter : Converter<String, SocialType> {
    override fun convert(source: String): SocialType = SocialType.from(source)
}
