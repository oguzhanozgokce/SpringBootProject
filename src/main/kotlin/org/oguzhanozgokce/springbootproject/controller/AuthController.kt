package org.oguzhanozgokce.springbootproject.controller

import jakarta.validation.Valid
import org.oguzhanozgokce.springbootproject.dto.ApiResponse
import org.oguzhanozgokce.springbootproject.dto.AuthResponse
import org.oguzhanozgokce.springbootproject.dto.LoginRequest
import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class AuthController(
    private val authService: AuthService
) {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<AuthResponse>> {

        if (bindingResult.hasErrors()) {
            val errors = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            logger.warn("Registration validation failed: $errors")
            return ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = "Validation failed: $errors",
                    data = null
                )
            )
        }

        return try {
            logger.info("Attempting to register user: ${registerRequest.username}")
            val authResponse = authService.register(registerRequest)
            logger.info("User registered successfully: ${registerRequest.username}")

            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(
                    success = true,
                    message = "User registered successfully",
                    data = authResponse
                )
            )
        } catch (e: IllegalArgumentException) {
            logger.warn("Registration failed for user ${registerRequest.username}: ${e.message}")
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "Registration failed",
                    data = null
                )
            )
        } catch (e: Exception) {
            logger.error("Unexpected error during registration for user ${registerRequest.username}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Internal server error occurred",
                    data = null
                )
            )
        }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
        bindingResult: BindingResult
    ): ResponseEntity<ApiResponse<AuthResponse>> {

        if (bindingResult.hasErrors()) {
            val errors = bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
            logger.warn("Login validation failed: $errors")
            return ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = "Validation failed: $errors",
                    data = null
                )
            )
        }

        return try {
            logger.info("Login attempt for user: ${loginRequest.username}")
            val authResponse = authService.login(loginRequest)
            logger.info("User logged in successfully: ${loginRequest.username}")

            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "Login successful",
                    data = authResponse
                )
            )
        } catch (e: BadCredentialsException) {
            logger.warn("Invalid credentials for user: ${loginRequest.username}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = "Invalid username or password",
                    data = null
                )
            )
        } catch (e: IllegalArgumentException) {
            logger.warn("Login failed for user ${loginRequest.username}: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = "Invalid username or password",
                    data = null
                )
            )
        } catch (e: Exception) {
            logger.error("Unexpected error during login for user ${loginRequest.username}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Internal server error occurred",
                    data = null
                )
            )
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val token = authHeader.substring(7) // Remove "Bearer " prefix
            val refreshResponse = authService.refreshToken(token)

            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "Token refreshed successfully",
                    data = refreshResponse
                )
            )
        } catch (e: Exception) {
            logger.warn("Token refresh failed: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = "Invalid or expired token",
                    data = null
                )
            )
        }
    }
}