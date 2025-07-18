package nmnb.application.global.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nmnb.application.global.auth.domain.CustomUserDetails
import nmnb.common.response.exception.AuthException
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import nmnb.common.utils.HeaderConstants.ACCESS_TOKEN_HEADER
import nmnb.application.global.common.utils.ResponseUtils
import nmnb.domain.user.repository.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTFilter(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val blacklistService: BlacklistService,
    private val responseUtils: ResponseUtils,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken = request.getHeader(ACCESS_TOKEN_HEADER)

        if (accessToken == null) {
            responseUtils.sendErrorResponse(response, ErrorStatus.AUTH_ACCESS_TOKEN_MISSING.getReasonHttpStatus())
            return
        }

        try {
            if (blacklistService.isBlacklisted(accessToken)) {
                throw AuthException(ErrorStatus.TOKEN_LOGGED_OUT)
            }

            val email = jwtProvider.getEmailWithValidation(accessToken)
            val user = userRepository.findByEmail(email)
                ?: throw AuthException(ErrorStatus.USER_NOT_FOUND)

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
            responseUtils.sendErrorResponse(response, e.getErrorReasonHttpStatus())
            return
        } catch (e: Exception) {
            responseUtils.sendErrorResponse(response, ErrorStatus.UNAUTHORIZED.getReasonHttpStatus())
            return
        }

        filterChain.doFilter(request, response)
    }
}
