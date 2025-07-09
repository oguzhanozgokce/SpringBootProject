package org.oguzhanozgokce.springbootproject.controller

import org.oguzhanozgokce.springbootproject.dto.ApiResponse
import org.oguzhanozgokce.springbootproject.dto.AuthResponse
import org.oguzhanozgokce.springbootproject.dto.LoginRequest
import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = authService.register(registerRequest)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "User registered successfully",
                    data = authResponse
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "Registration failed",
                    data = null
                )
            )
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = authService.login(loginRequest)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    message = "Login successful",
                    data = authResponse
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "Login failed",
                    data = null
                )
            )
        }
    }
}