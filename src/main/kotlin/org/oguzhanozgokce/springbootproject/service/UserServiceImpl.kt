package org.oguzhanozgokce.springbootproject.service

import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.model.Role
import org.oguzhanozgokce.springbootproject.model.User
import org.oguzhanozgokce.springbootproject.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Value("\${app.base-url}")
    private lateinit var baseUrl: String

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

    override fun updateUserProfileImage(userId: Long, file: MultipartFile): UserResponse? {
        val user = userRepository.findById(userId).orElse(null) ?: return null

        // Validate file type
        val allowedTypes = listOf("image/jpeg", "image/jpg", "image/png", "image/gif")
        if (!allowedTypes.contains(file.contentType)) {
            throw IllegalArgumentException("Invalid file type. Only JPEG, PNG and GIF files are allowed.")
        }

        // Validate file size (max 20MB)
        if (file.size > 20 * 1024 * 1024) {
            throw IllegalArgumentException("File size too large. Maximum size is 20MB.")
        }

        try {
            // Create unique filename
            val fileExtension = file.originalFilename?.substringAfterLast('.') ?: "jpg"
            val fileName = "profile_${userId}_${UUID.randomUUID()}.$fileExtension"

            // Create upload directory if it doesn't exist
            val uploadDir = File("src/main/resources/static/uploads")
            if (!uploadDir.exists()) {
                uploadDir.mkdirs()
            }

            // Save file
            val filePath = Paths.get(uploadDir.path, fileName)
            Files.copy(file.inputStream, filePath)

            // Delete old profile image if exists
            user.profileImageUrl?.let { oldImageUrl ->
                val oldFileName = oldImageUrl.substringAfterLast("/")
                val oldFilePath = Paths.get(uploadDir.path, oldFileName)
                Files.deleteIfExists(oldFilePath)
            }

            // Update user with new image URL (with base URL)
            val imageUrl = "$baseUrl/uploads/$fileName"
            val updatedUser = User(
                id = user.id,
                username = user.username,
                email = user.email,
                password = user.password,
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role,
                enabled = user.enabled,
                createdAt = user.createdAt,
                updatedAt = LocalDateTime.now(),
                profileImageUrl = imageUrl
            )

            val savedUser = userRepository.save(updatedUser)
            return convertToUserResponse(savedUser)

        } catch (e: Exception) {
            throw RuntimeException("Failed to upload profile image: ${e.message}")
        }
    }

    private fun convertToUserResponse(user: User): UserResponse {
        val fullImageUrl = user.profileImageUrl?.let { imageUrl ->
            if (imageUrl.startsWith("http")) {
                imageUrl
            } else {
                "$baseUrl$imageUrl"
            }
        }

        return UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            role = user.role.name,
            profileImageUrl = fullImageUrl
        )
    }
}