package org.oguzhanozgokce.springbootproject.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request)

            if (jwt != null && SecurityContextHolder.getContext().authentication == null) {
                authenticateUser(jwt, request)
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication in security context", e)
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else null
    }

    private fun authenticateUser(jwt: String, request: HttpServletRequest) {
        try {
            val username = jwtUtil.extractUsername(jwt)

            if (username.isNullOrBlank()) {
                logger.debug("Username is null or blank in JWT token")
                return
            }

            val userDetails: UserDetails = try {
                userDetailsService.loadUserByUsername(username)
            } catch (e: UsernameNotFoundException) {
                logger.debug("User not found: $username")
                return
            }

            if (jwtUtil.validateToken(jwt, userDetails)) {
                logger.debug("JWT token is valid for user: $username")

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication

                logger.debug("User authenticated successfully: $username")
            } else {
                logger.debug("JWT token is invalid for user: $username")
            }
        } catch (e: Exception) {
            logger.debug("JWT token validation failed", e)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI

        // Skip authentication for public endpoints
        val publicPaths = listOf(
            "/api/auth/login",
            "/api/auth/register",
            "/h2-console",
            "/actuator/health",
            "/error"
        )

        return publicPaths.any { path.startsWith(it) }
    }
}