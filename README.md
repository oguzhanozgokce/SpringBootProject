# Spring Boot Backend Project

[🇺🇸 English](#english) | [🇹🇷 Türkçe](#turkish)

---

## English

This project is a modern Spring Boot 3.5.3 enterprise-grade backend application developed using Clean Architecture and
SOLID principles.

## Features

- **Clean Architecture**: Layered architectural structure
- **SOLID Principles**: Code quality and maintainability
- **MVC Pattern**: Model-View-Controller structure
- **JWT Authentication**: Token-based authentication + refresh token
- **Spring Security**: Role-based access control and method-level security
- **JPA/Hibernate**: Database operations
- **H2 Database**: In-memory database for development
- **Bean Validation**: Request validation
- **Structured Logging**: Audit logging with SLF4J
- **CORS Support**: Cross-origin resource sharing
- **Pagination**: Pagination support
- **Method Security**: Endpoint protection with @PreAuthorize
- **Comprehensive Testing**: Integration tests with MockMvc
- **Global Exception Handling**: Centralized error management
- **Custom Response Logging**: Clean test output formatting

## Testing System

### Test Coverage:

- ✅ **Authentication Tests**: Registration, login, token refresh
- ✅ **Validation Tests**: Input validation errors
- ✅ **Authorization Tests**: Role-based access control
- ✅ **Exception Handling Tests**: GlobalExceptionHandler integration
- ✅ **Success/Error Scenarios**: Complete flow testing

### Test Features:

```kotlin
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = ["spring.profiles.active=test"])
@Transactional
class SpringBootProjectApplicationTests {

    // Custom clean response logging
    private fun printEssentials(): ResultHandler = ResultHandler { result ->
        println("=== HTTP Response ===")
        println("Status: ${result.response.status}")
        println("Content-Type: ${result.response.contentType}")
        println("Body: ${result.response.contentAsString}")
        println("=====================")
    }
}
```

### Test Examples:

- **Register Success**: Valid user registration
- **Register Fail**: Invalid email, short password, duplicate username
- **Login Success**: Valid credentials
- **Login Fail**: Invalid credentials, validation errors
- **Token Refresh**: Valid/invalid token scenarios

### Running Tests:

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "*register should succeed*"

# Run with detailed output
./gradlew test --info
```

## Logging System

### Structured Logging:

- **Framework**: SLF4J with Logback
- **Levels**: INFO, WARN, ERROR, DEBUG
- **Categories**: Security, Application, Error logging
- **Format**: Structured JSON-like format for production

### Log Categories:

```kotlin
// Authentication logs
logger.info("Login attempt for user: ${username}")
logger.info("User registered successfully: ${username}")
logger.warn("Registration validation failed: ${errors}")

// Security logs
logger.warn("Invalid credentials for user: ${username}")
logger.error("Unexpected error during login", exception)
```

### Log Files:

- `logs/app.log` - Application logs
- `logs/security.log` - Security events
- `logs/error.log` - Error tracking

## Package Structure

```
src/main/kotlin/org/oguzhanozgokce/springbootproject/
├── controller/          # REST API endpoints
│   ├── AuthController   # Authentication endpoints
│   └── UserController   # User management endpoints
├── service/            # Business logic
│   ├── AuthService     # Authentication service
│   ├── UserService     # User service interface
│   └── UserServiceImpl # User service implementation
├── repository/         # Data access layer
├── model/             # Entity classes
├── dto/               # Data Transfer Objects
├── config/            # Configuration classes
├── security/          # Security utilities
│   ├── JwtUtil        # JWT token utilities
│   └── JwtAuthenticationFilter # JWT authentication filter
└── exception/         # Exception handling
    └── GlobalExceptionHandler # Centralized error handling
```

## API Endpoints

### Authentication Endpoints

#### 1. **POST** `/api/auth/register` - User registration

**Request Body:**

```json
{
  "username": "oguzhan",
  "email": "oguzhan@example.com",
  "password": "password123",
  "firstName": "Oğuzhan",
  "lastName": "Özgökçe"
}
```

**Response:**

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "oguzhan",
      "email": "oguzhan@example.com",
      "firstName": "Oğuzhan",
      "lastName": "Özgökçe",
      "role": "USER"
    }
  }
}
```

#### 2. **POST** `/api/auth/login` - User login

**Request Body:**

```json
{
  "username": "oguzhan",
  "password": "password123"
}
```

#### 3. **POST** `/api/auth/refresh` - Token refresh

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

### User Management Endpoints

#### 4. **GET** `/api/users/profile` - Current user profile

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Permission:** USER, ADMIN

#### 5. **GET** `/api/users/{id}` - User details

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Permission:** ADMIN or own profile

#### 6. **GET** `/api/users?page=0&size=10` - All users (pagination)

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Permission:** ADMIN only

#### 7. **DELETE** `/api/users/{id}` - Delete user

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Permission:** ADMIN or own account

#### 8. **PUT** `/api/users/{id}/role?role=ADMIN` - Update user role

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Permission:** ADMIN only

## Validation Rules

### Register Request:

- **username**: 3-20 characters, not empty
- **email**: Valid email format, not empty
- **password**: Minimum 6 characters, not empty
- **firstName**: 2-50 characters, not empty
- **lastName**: 2-50 characters, not empty

### Login Request:

- **username**: 3-20 characters, not empty
- **password**: Minimum 6 characters, not empty

## Security Features

### JWT Token:

- **Algorithm**: HS512
- **Duration**: 24 hours (86400000 ms)
- **Refresh**: Renewable via `/api/auth/refresh` endpoint

### Role-Based Access Control:

- **USER**: Can view, update, delete own profile
- **ADMIN**: Can manage all users, change roles

### Method Security:

- Endpoint-level protection with `@PreAuthorize`
- Expression-based access control
- Security monitoring with audit logging

## Running

### 1. **Requirements:**

- Java 17+
- Gradle 8.x

### 2. **Clone the project:**

```bash
git clone <repository-url>
cd SpringBootProject
```

### 3. **Install dependencies:**

```bash
./gradlew build
```

### 4. **Run the application:**

```bash
./gradlew bootRun
```

The application will run at `http://localhost:8082`.

## API Testing

### Postman Collection Example:

#### 1. Register:

```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "oguzhan",
    "email": "oguzhan@example.com",
    "password": "password123",
    "firstName": "Oğuzhan",
    "lastName": "Özgökçe"
  }'
```

#### 2. Login:

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "oguzhan",
    "password": "password123"
  }'
```

#### 3. Profile:

```bash
curl -X GET http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Database

### H2 Console:

- **URL**: http://localhost:8082/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### Database Schema:

```sql
CREATE TABLE users
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(255) UNIQUE NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    first_name VARCHAR(255)        NOT NULL,
    last_name  VARCHAR(255)        NOT NULL,
    role       ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    enabled    BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP           NOT NULL
);
```

## Technologies

- **Kotlin** - Modern JVM language
- **Spring Boot 3.5.3** - Framework
- **Spring Security 6.x** - Security
- **Spring Data JPA** - Database access
- **Bean Validation** - Request validation
- **JWT (JSON Web Token)** - Authentication
- **H2 Database** - In-memory database
- **SLF4J** - Logging
- **Gradle** - Build tool
- **JUnit 5** - Testing framework
- **MockMvc** - Integration testing

---

## Turkish

Bu proje Clean Architecture ve SOLID prensipleri kullanılarak geliştirilmiş, modern Spring Boot 3.5.3 ile
enterprise-grade bir backend uygulamasıdır.

## Özellikler

- **Clean Architecture**: Katmanlı mimari yapısı
- **SOLID Prensipler**: Kod kalitesi ve sürdürülebilirlik
- **MVC Pattern**: Model-View-Controller yapısı
- **JWT Authentication**: Token tabanlı kimlik doğrulama + refresh token
- **Spring Security**: Role-based access control ve method-level security
- **JPA/Hibernate**: Veritabanı işlemleri
- **H2 Database**: Geliştirme için in-memory veritabanı
- **Bean Validation**: Request validation
- **Yapılandırılmış Loglama**: SLF4J ile audit logging
- **CORS Desteği**: Cross-origin resource sharing
- **Sayfalama**: Pagination desteği
- **Method Security**: @PreAuthorize ile endpoint koruma
- **Kapsamlı Test Sistemi**: MockMvc ile integration testleri
- **Global Exception Handling**: Merkezi hata yönetimi
- **Özel Response Loglama**: Temiz test output formatı

## Test Sistemi

### Test Kapsamı:

- ✅ **Authentication Testleri**: Kayıt, giriş, token yenileme
- ✅ **Validation Testleri**: Input validation hataları
- ✅ **Authorization Testleri**: Role-based access control
- ✅ **Exception Handling Testleri**: GlobalExceptionHandler entegrasyonu
- ✅ **Başarılı/Hatalı Senaryolar**: Tam flow testleri

### Test Özellikleri:

```kotlin
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = ["spring.profiles.active=test"])
@Transactional
class SpringBootProjectApplicationTests {
    
    // Özel temiz response loglama
    private fun printEssentials(): ResultHandler = ResultHandler { result ->
        println("=== HTTP Response ===")
        println("Status: ${result.response.status}")
        println("Content-Type: ${result.response.contentType}")
        println("Body: ${result.response.contentAsString}")
        println("=====================")
    }
}
```

### Test Örnekleri:

- **Register Başarılı**: Geçerli kullanıcı kaydı
- **Register Başarısız**: Geçersiz email, kısa şifre, duplicate username
- **Login Başarılı**: Geçerli kimlik bilgileri
- **Login Başarısız**: Geçersiz kimlik bilgileri, validation hataları
- **Token Yenileme**: Geçerli/geçersiz token senaryoları

### Testleri Çalıştırma:

```bash
# Tüm testleri çalıştır
./gradlew test

# Belirli testi çalıştır
./gradlew test --tests "*register should succeed*"

# Detaylı output ile çalıştır
./gradlew test --info
```

## Loglama Sistemi

### Yapılandırılmış Loglama:

- **Framework**: SLF4J ile Logback
- **Seviyeler**: INFO, WARN, ERROR, DEBUG
- **Kategoriler**: Security, Application, Error loglama
- **Format**: Production için yapılandırılmış JSON benzeri format

### Log Kategorileri:

```kotlin
// Authentication logları
logger.info("Login attempt for user: ${username}")
logger.info("User registered successfully: ${username}")
logger.warn("Registration validation failed: ${errors}")

// Security logları
logger.warn("Invalid credentials for user: ${username}")
logger.error("Unexpected error during login", exception)
```

### Log Dosyaları:

- `logs/app.log` - Uygulama logları
- `logs/security.log` - Güvenlik olayları
- `logs/error.log` - Hata takibi

## Paket Yapısı

```
src/main/kotlin/org/oguzhanozgokce/springbootproject/
├── controller/          # REST API endpoints
│   ├── AuthController   # Authentication endpoints
│   └── UserController   # User management endpoints
├── service/            # Business logic
│   ├── AuthService     # Authentication service
│   ├── UserService     # User service interface
│   └── UserServiceImpl # User service implementation
├── repository/         # Data access layer
├── model/             # Entity sınıfları
├── dto/               # Data Transfer Objects
├── config/            # Configuration sınıfları
├── security/          # Security utilities
│   ├── JwtUtil        # JWT token utilities
│   └── JwtAuthenticationFilter # JWT authentication filter
└── exception/         # Exception handling
    └── GlobalExceptionHandler # Merkezi hata yönetimi
```

## API Endpoints

### Authentication Endpoints

#### 1. **POST** `/api/auth/register` - Kullanıcı kaydı

**Request Body:**
```json
{
  "username": "oguzhan",
  "email": "oguzhan@example.com",
  "password": "password123",
  "firstName": "Oğuzhan",
  "lastName": "Özgökçe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "oguzhan",
      "email": "oguzhan@example.com",
      "firstName": "Oğuzhan",
      "lastName": "Özgökçe",
      "role": "USER"
    }
  }
}
```

#### 2. **POST** `/api/auth/login` - Kullanıcı girişi

**Request Body:**

```json
{
  "username": "oguzhan",
  "password": "password123"
}
```

#### 3. **POST** `/api/auth/refresh` - Token yenileme

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

### User Management Endpoints

#### 4. **GET** `/api/users/profile` - Mevcut kullanıcı profili

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Yetki:** USER, ADMIN

#### 5. **GET** `/api/users/{id}` - Kullanıcı detayı

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Yetki:** ADMIN veya kendi profili

#### 6. **GET** `/api/users?page=0&size=10` - Tüm kullanıcılar (sayfalama)

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Yetki:** ADMIN only

#### 7. **DELETE** `/api/users/{id}` - Kullanıcı silme

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Yetki:** ADMIN veya kendi hesabı

#### 8. **PUT** `/api/users/{id}/role?role=ADMIN` - Kullanıcı rolü güncelleme

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Yetki:** ADMIN only

## Validation Kuralları

### Register Request:

- **username**: 3-20 karakter arası, boş olamaz
- **email**: Geçerli email formatı, boş olamaz
- **password**: Minimum 6 karakter, boş olamaz
- **firstName**: 2-50 karakter arası, boş olamaz
- **lastName**: 2-50 karakter arası, boş olamaz

### Login Request:

- **username**: 3-20 karakter arası, boş olamaz
- **password**: Minimum 6 karakter, boş olamaz

## Güvenlik Özellikleri

### JWT Token:

- **Algoritma**: HS512
- **Süre**: 24 saat (86400000 ms)
- **Refresh**: `/api/auth/refresh` endpoint'i ile yenilenebilir

### Role-Based Access Control:

- **USER**: Kendi profilini görüntüleyebilir, güncelleyebilir, silebilir
- **ADMIN**: Tüm kullanıcıları yönetebilir, rol değiştirebilir

### Method Security:

- `@PreAuthorize` ile endpoint-level koruma
- Expression-based access control
- Audit logging ile güvenlik izleme

## Çalıştırma

### 1. **Gereksinimler:**

- Java 17+
- Gradle 8.x

### 2. **Projeyi klonlayın:**

```bash
git clone <repository-url>
cd SpringBootProject
```

### 3. **Bağımlılıkları yükleyin:**

```bash
./gradlew build
```

### 4. **Uygulamayı çalıştırın:**

```bash
./gradlew bootRun
```

Uygulama `http://localhost:8082` adresinde çalışacaktır.

## API Testi

### Postman Collection Örneği:

#### 1. Register:

```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "oguzhan",
    "email": "oguzhan@example.com",
    "password": "password123",
    "firstName": "Oğuzhan",
    "lastName": "Özgökçe"
  }'
```

#### 2. Login:

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "oguzhan",
    "password": "password123"
  }'
```

#### 3. Profile:

```bash
curl -X GET http://localhost:8082/api/users/profile \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Veritabanı

### H2 Console:

- **URL**: http://localhost:8082/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### Database Schema:

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## Teknolojiler

- **Kotlin** - Modern JVM dili
- **Spring Boot 3.5.3** - Framework
- **Spring Security 6.x** - Güvenlik
- **Spring Data JPA** - Veritabanı erişimi
- **Bean Validation** - Request validation
- **JWT (JSON Web Token)** - Authentication
- **H2 Database** - In-memory database
- **SLF4J** - Logging
- **Gradle** - Build tool
- **JUnit 5** - Test framework
- **MockMvc** - Integration testing

## Önemli Notlar

### Production Hazırlığı:

- **Yapılandırılmış Loglama** - Audit trail için
- **Input Validation** - Güvenlik için
- **Error Handling** - Proper HTTP status codes
- **Security Headers** - CORS, CSRF protection
- **Method Security** - Fine-grained access control
- **Pagination** - Performance için
- **JWT Refresh** - Token yenileme mekanizması
- **Kapsamlı Test Suit** - Quality assurance için

### Development vs Production:
- **Development**: H2 in-memory database
- **Production**: PostgreSQL ile değiştir
- **JWT Secret**: Production'da environment variable kullan
- **Logging**: Production'da log level ayarla

Bu proje enterprise-grade Spring Boot uygulaması için best practices kullanarak geliştirilmiştir.