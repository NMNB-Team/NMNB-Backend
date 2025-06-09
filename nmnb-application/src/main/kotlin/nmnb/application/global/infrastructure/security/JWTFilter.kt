package nmnb.application.global.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nmnb.application.global.auth.domain.CustomUserDetails
import nmnb.common.response.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import nmnb.domain.user.repository.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER)

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
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }

                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: GeneralException) {
                throw AuthException(ErrorStatus.UNAUTHORIZED)
            }
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
