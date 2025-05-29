package nmnb.application.global.infrastructure.external

import nmnb.application.global.auth.exception.AuthException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.auth.SocialType
import org.springframework.stereotype.Component

@Component
class OAuthClientComposite(
    private val clients: List<OAuthClient>,
) {
    fun getClient(type: SocialType): OAuthClient =
        clients.find { it.supports(type) }
            ?: throw AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE)
}
