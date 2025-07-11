package org.oguzhanozgokce.springbootproject

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.oguzhanozgokce.springbootproject.dto.LoginRequest
import org.oguzhanozgokce.springbootproject.dto.RegisterRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = ["spring.profiles.active=test"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class SpringBootProjectApplicationTests {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    private fun printEssentials(): ResultHandler = ResultHandler { result ->
        println("=== HTTP Response ===")
        println("Status: ${result.response.status}")
        println("Content-Type: ${result.response.contentType}")
        println("Body: ${result.response.contentAsString}")
        println("=====================")
    }

    @Test
    fun contextLoads() {
        // Basic context loading test
    }

    @Test
    fun `register should succeed with valid data`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.data.token").exists())
            .andExpect(jsonPath("$.data.user.username").value("testuser"))
            .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
    }

    @Test
    fun `register should fail with invalid email`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "invalid-email",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Email should be valid")))
    }

    @Test
    fun `register should fail with short password`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "123",
            firstName = "Test",
            lastName = "User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Password must be at least 6 characters")))
    }

    @Test
    fun `register should fail with duplicate username - GlobalExceptionHandler test`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )

        // First registration
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isCreated)

        val duplicateRequest = registerRequest.copy(email = "different@example.com")
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `login should succeed with valid credentials`() {
        val registerRequest = RegisterRequest(
            username = "loginuser",
            email = "login@example.com",
            password = "password123",
            firstName = "Login",
            lastName = "User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isCreated)

        // Then login
        val loginRequest = LoginRequest(
            username = "loginuser",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(jsonPath("$.data.token").exists())
            .andExpect(jsonPath("$.data.user.username").value("loginuser"))
    }

    @Test
    fun `login should fail with invalid credentials - GlobalExceptionHandler test`() {
        val loginRequest = LoginRequest(
            username = "nonexistent",
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Invalid username or password"))
    }

    @Test
    fun `login should fail with validation errors`() {
        val loginRequest = LoginRequest(
            username = "ab",
            password = "123"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")))
    }

    @Test
    fun `refresh token should fail with invalid token - GlobalExceptionHandler test`() {
        mockMvc.perform(
            post("/api/auth/refresh")
                .header("Authorization", "Bearer invalid-token")
        )
            .andDo(printEssentials())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `refresh token should succeed with valid token`() {
        // First register and get token
        val registerRequest = RegisterRequest(
            username = "refreshuser",
            email = "refresh@example.com",
            password = "password123",
            firstName = "Refresh",
            lastName = "User"
        )

        val registrationResult = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(printEssentials())
            .andExpect(status().isCreated)
            .andReturn()

        val response = objectMapper.readTree(registrationResult.response.contentAsString)
        val token = response.get("data").get("token").asText()

        // Then refresh token
        mockMvc.perform(
            post("/api/auth/refresh")
                .header("Authorization", "Bearer $token")
        )
            .andDo(printEssentials())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
            .andExpect(jsonPath("$.data.token").exists())
    }
}
