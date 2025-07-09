package org.oguzhanozgokce.springbootproject.dto

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val user: UserResponse
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)