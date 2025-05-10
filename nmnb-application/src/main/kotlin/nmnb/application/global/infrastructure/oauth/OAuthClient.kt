package nmnb.application.global.infrastructure.oauth

import nmnb.application.global.auth.service.dto.OAuthProfile
import nmnb.domain.auth.SocialType

interface OAuthClient {
    fun requestProfile(accessCode: String): OAuthProfile
    fun supports(type: SocialType): Boolean
}
