package nmnb.application.global.auth.utils

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import nmnb.application.global.auth.domain.CustomUserDetails
import nmnb.application.global.auth.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.user.repository.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class JWTFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as? HttpServletRequest
            ?: throw IllegalArgumentException("Request is not HttpServletRequest")

        val authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER)

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                val token = authorizationHeader.substring(7)
                val email = jwtTokenProvider.getEmailWithValidation(token)
                val user = userRepository.findByEmail(email)
                    ?: throw GeneralException(ErrorStatus.USER_NOT_FOUND)

                val userDetails = CustomUserDetails(user)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities,
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(httpRequest)
                }

                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: GeneralException) {
                throw AuthException(ErrorStatus.UNAUTHORIZED)
            }
        }

        chain?.doFilter(request, response)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
