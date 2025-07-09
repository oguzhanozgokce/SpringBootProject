package org.oguzhanozgokce.springbootproject.service

import org.oguzhanozgokce.springbootproject.dto.AuthResponse
import org.oguzhanozgokce.springbootproject.dto.LoginRequest
import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

    fun register(registerRequest: RegisterRequest): AuthResponse {
        val userResponse = userService.registerUser(registerRequest)
        val user = userService.findByUsername(registerRequest.username)
            ?: throw IllegalStateException("User not found after registration")

        val token = jwtUtil.generateToken(user)

        return AuthResponse(
            token = token,
            user = userResponse
        )
    }

    fun login(loginRequest: LoginRequest): AuthResponse {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password
            )
        )

        val user = userService.findByUsername(loginRequest.username)
            ?: throw IllegalArgumentException("User not found")

        val token = jwtUtil.generateToken(user)

        val userResponse = UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role.name
        )

        return AuthResponse(
            token = token,
            user = userResponse
        )
    }

    fun refreshToken(oldToken: String): AuthResponse {
        val username = jwtUtil.extractUsername(oldToken)
            ?: throw IllegalArgumentException("Invalid token")

        val user = userService.findByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        if (!jwtUtil.validateToken(oldToken, user)) {
            throw IllegalArgumentException("Invalid or expired token")
        }

        val newToken = jwtUtil.generateToken(user)
        val userResponse = UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role.name
        )

        return AuthResponse(
            token = newToken,
            user = userResponse
        )
    }
}