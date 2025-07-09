# Spring Boot Backend Project

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
- **Structured Logging**: SLF4J ile audit logging
- **CORS Support**: Cross-origin resource sharing
- **Pagination**: Sayfalama desteği
- **Method Security**: @PreAuthorize ile endpoint koruma

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

#### 4. Admin - Tüm kullanıcılar:

```bash
curl -X GET "http://localhost:8082/api/users?page=0&size=10" \
  -H "Authorization: Bearer <admin-jwt-token>"
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

## Önemli Notlar

### Production Hazırlığı:

- **Structured Logging** - Audit trail için
- **Input Validation** - Güvenlik için
- **Error Handling** - Proper HTTP status codes
- **Security Headers** - CORS, CSRF protection
- **Method Security** - Fine-grained access control
- **Pagination** - Performance için
- **JWT Refresh** - Token yenileme mekanizması

### Development vs Production:

- **Development**: H2 in-memory database
- **Production**: PostgreSQL ile değiştir
- **JWT Secret**: Production'da environment variable kullan
- **Logging**: Production'da log level ayarla

Bu proje enterprise-grade Spring Boot uygulaması için best practices kullanarak geliştirilmiştir.