package org.oguzhanozgokce.springbootproject.service

import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.model.Role
import org.oguzhanozgokce.springbootproject.model.User
import org.oguzhanozgokce.springbootproject.repository.UserRepository
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

    override fun getUserById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
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