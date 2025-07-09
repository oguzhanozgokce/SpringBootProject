package org.oguzhanozgokce.springbootproject.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private var expiration: Long = 0

    private val key: SecretKey by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return try {
            extractClaim(token, Claims::getSubject)
        } catch (e: Exception) {
            null
        }
    }

    fun extractExpiration(token: String): Date? {
        return try {
            extractClaim(token, Claims::getExpiration)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String): Boolean {
        val expiration = extractExpiration(token)
        return expiration?.before(Date()) ?: true
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        return try {
            val username = extractUsername(token)
            username == userDetails.username && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
}