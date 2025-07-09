package org.oguzhanozgokce.springbootproject.service

import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.model.User

interface UserService {
    fun registerUser(registerRequest: RegisterRequest): UserResponse
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun getUserById(id: Long): User?
}