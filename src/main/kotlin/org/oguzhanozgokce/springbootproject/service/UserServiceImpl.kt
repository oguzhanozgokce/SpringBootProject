package org.oguzhanozgokce.springbootproject.service

import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.model.Role
import org.oguzhanozgokce.springbootproject.model.User
import org.oguzhanozgokce.springbootproject.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun registerUser(registerRequest: RegisterRequest): UserResponse {
        if (existsByUsername(registerRequest.username)) {
            throw IllegalArgumentException("Username already exists")
        }
        if (existsByEmail(registerRequest.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
            firstName = registerRequest.firstName,
            lastName = registerRequest.lastName,
            role = Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(user)
        return convertToUserResponse(savedUser)
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username).orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }

    override fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override fun getUserById(id: Long): UserResponse? {
        val user = userRepository.findById(id).orElse(null)
        return user?.let { convertToUserResponse(it) }
    }

    override fun deleteUser(id: Long): Boolean {
        return try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun getCurrentUser(username: String): UserResponse? {
        val user = findByUsername(username)
        return user?.let { convertToUserResponse(it) }
    }

    override fun getAllUsers(page: Int, size: Int): List<UserResponse> {
        val pageable = PageRequest.of(page, size)
        return userRepository.findAll(pageable).content.map { convertToUserResponse(it) }
    }

    override fun updateUserRole(id: Long, role: String): UserResponse? {
        val user = userRepository.findById(id).orElse(null) ?: return null

        val newRole = try {
            Role.valueOf(role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid role: $role. Valid roles are: ${Role.values().joinToString(", ")}")
        }

        // Create a new user instance with updated role
        val updatedUser = User(
            id = user.id,
            username = user.username,
            email = user.email,
            password = user.password,
            firstName = user.firstName,
            lastName = user.lastName,
            role = newRole,
            enabled = user.enabled,
            createdAt = user.createdAt,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return convertToUserResponse(savedUser)
    }

    private fun convertToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role.name
        )
    }
}