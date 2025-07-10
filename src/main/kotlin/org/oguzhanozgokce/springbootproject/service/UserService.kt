package org.oguzhanozgokce.springbootproject.service

import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.model.User
import org.springframework.web.multipart.MultipartFile

interface UserService {
    fun registerUser(registerRequest: RegisterRequest): UserResponse
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun getUserById(id: Long): UserResponse?
    fun deleteUser(id: Long): Boolean
    fun getCurrentUser(username: String): UserResponse?
    fun getAllUsers(page: Int, size: Int): List<UserResponse>
    fun updateUserRole(id: Long, role: String): UserResponse?
    fun updateUserProfileImage(userId: Long, file: MultipartFile): UserResponse?
}