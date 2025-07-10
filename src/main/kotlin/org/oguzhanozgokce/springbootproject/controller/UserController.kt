package org.oguzhanozgokce.springbootproject.controller

import org.oguzhanozgokce.springbootproject.dto.ApiResponse
import org.oguzhanozgokce.springbootproject.dto.UserResponse
import org.oguzhanozgokce.springbootproject.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class UserController(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun getCurrentUserProfile(): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val authentication: Authentication = SecurityContextHolder.getContext().authentication
            val username = authentication.name

            logger.info("Fetching profile for user: $username")
            val userResponse = userService.getCurrentUser(username)

            if (userResponse != null) {
                logger.info("Profile retrieved successfully for user: $username")
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "User profile retrieved successfully",
                        data = userResponse
                    )
                )
            } else {
                logger.warn("User profile not found: $username")
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse(
                        success = false,
                        message = "User not found",
                        data = null
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Error retrieving user profile", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Internal server error occurred",
                    data = null
                )
            )
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userService.getCurrentUser(authentication.name)?.id == #id)")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val authentication: Authentication = SecurityContextHolder.getContext().authentication
            logger.info("User ${authentication.name} requesting user details for ID: $id")

            val userResponse = userService.getUserById(id)

            if (userResponse != null) {
                logger.info("User details retrieved successfully for ID: $id")
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "User retrieved successfully",
                        data = userResponse
                    )
                )
            } else {
                logger.warn("User not found with ID: $id")
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse(
                        success = false,
                        message = "User not found with id: $id",
                        data = null
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Error retrieving user with ID: $id", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Internal server error occurred",
                    data = null
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userService.getCurrentUser(authentication.name)?.id == #id)")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            val authentication: Authentication = SecurityContextHolder.getContext().authentication
            val currentUser = userService.getCurrentUser(authentication.name)

            if (currentUser == null) {
                logger.warn("Current user not found during delete operation")
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse(
                        success = false,
                        message = "User not authenticated",
                        data = null
                    )
                )
            }

            logger.info("User ${authentication.name} attempting to delete user with ID: $id")

            // Prevent users from deleting admin accounts (unless they are admin themselves)
            val targetUser = userService.getUserById(id)
            if (targetUser?.role == "ADMIN" && currentUser.role != "ADMIN") {
                logger.warn("Non-admin user ${authentication.name} attempted to delete admin account")
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ApiResponse(
                        success = false,
                        message = "Cannot delete admin accounts",
                        data = null
                    )
                )
            }

            val isDeleted = userService.deleteUser(id)

            if (isDeleted) {
                logger.info("User deleted successfully - ID: $id by user: ${authentication.name}")
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "User deleted successfully",
                        data = "User with id $id has been deleted"
                    )
                )
            } else {
                logger.warn("User not found for deletion - ID: $id")
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse(
                        success = false,
                        message = "User not found with id: $id",
                        data = null
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Error deleting user with ID: $id", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Internal server error occurred",
                    data = null
                )
            )
        }
    }

    @PostMapping("/profile/image")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun updateProfileImage(
        @RequestParam("image") file: MultipartFile
    ): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val authentication: Authentication = SecurityContextHolder.getContext().authentication
            val username = authentication.name

            logger.info("User $username uploading profile image")

            val currentUser = userService.getCurrentUser(username)
            if (currentUser == null) {
                logger.warn("User not found during image upload: $username")
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse(
                        success = false,
                        message = "User not found",
                        data = null
                    )
                )
            }

            val updatedUser = userService.updateUserProfileImage(currentUser.id, file)

            if (updatedUser != null) {
                logger.info("Profile image updated successfully for user: $username")
                ResponseEntity.ok(
                    ApiResponse(
                        success = true,
                        message = "Profile image updated successfully",
                        data = updatedUser
                    )
                )
            } else {
                logger.error("Failed to update profile image for user: $username")
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse(
                        success = false,
                        message = "Failed to update profile image",
                        data = null
                    )
                )
            }

        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid file upload: ${e.message}")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    success = false,
                    message = e.message ?: "Invalid file",
                    data = null
                )
            )
        }
        catch (e: Exception) {
            logger.error("Error uploading profile image", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    success = false,
                    message = "Internal server error occurred",
                    data = null
                )
            )
        }
    }
}