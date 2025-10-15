# VideoGameDB Project Structure

## 📁 Complete File Tree

```
videogamedb-api/
├── pom.xml                              # Maven configuration with dependencies
├── Dockerfile                           # Docker container configuration
├── .gitignore                          # Git ignore patterns
├── .env.example                        # Environment variable template
├── README.md                           # Comprehensive user documentation
├── IMPLEMENTATION_SUMMARY.md           # Implementation details and status
├── PROJECT_STRUCTURE.md                # This file
│
├── src/
│   ├── main/
│   │   ├── java/com/videogamedb/
│   │   │   ├── VideoGameDbApplication.java    # Spring Boot main class
│   │   │   │
│   │   │   ├── config/                        # Configuration classes
│   │   │   │   ├── SecurityConfig.java        # Spring Security setup
│   │   │   │   ├── MongoStartupRunner.java    # DB initialization
│   │   │   │   └── GlobalExceptionHandler.java # Error handling
│   │   │   │
│   │   │   ├── models/                        # Entity classes
│   │   │   │   ├── User.java                  # User with encrypted fields
│   │   │   │   ├── VideoGame.java             # Game catalog
│   │   │   │   ├── Platform.java              # Gaming platforms
│   │   │   │   ├── Genre.java                 # Game genres
│   │   │   │   ├── Contributor.java           # Developers/publishers
│   │   │   │   ├── PlatformRelease.java       # Game releases
│   │   │   │   ├── OwnedGame.java             # User ownership
│   │   │   │   ├── PlaySession.java           # Play tracking
│   │   │   │   ├── Rating.java                # User ratings
│   │   │   │   ├── AccessTime.java            # Access logging
│   │   │   │   ├── Follow.java                # Social follows
│   │   │   │   └── Collection.java            # Game collections
│   │   │   │
│   │   │   ├── repositories/                  # Data access layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── VideoGameRepository.java
│   │   │   │   ├── PlatformRepository.java
│   │   │   │   ├── GenreRepository.java
│   │   │   │   ├── ContributorRepository.java
│   │   │   │   ├── PlatformReleaseRepository.java
│   │   │   │   ├── OwnedGameRepository.java
│   │   │   │   ├── PlaySessionRepository.java
│   │   │   │   ├── RatingRepository.java
│   │   │   │   ├── AccessTimeRepository.java
│   │   │   │   ├── FollowRepository.java
│   │   │   │   └── CollectionRepository.java
│   │   │   │
│   │   │   ├── services/                      # Business logic
│   │   │   │   ├── UserService.java           # User management
│   │   │   │   └── DataGenerationService.java # Mock data generation
│   │   │   │
│   │   │   ├── controllers/                   # REST endpoints
│   │   │   │   ├── AuthController.java        # Authentication
│   │   │   │   ├── DataGeneratorController.java # Data generation
│   │   │   │   ├── VideoGameController.java   # Game CRUD
│   │   │   │   ├── PlatformController.java    # Platform CRUD
│   │   │   │   ├── RatingController.java      # Rating CRUD
│   │   │   │   ├── PlaySessionController.java # Session CRUD
│   │   │   │   ├── CollectionController.java  # Collection CRUD
│   │   │   │   ├── AccessTimeController.java  # Access time CRUD
│   │   │   │   ├── FollowController.java      # Follow CRUD
│   │   │   │   └── OwnedGameController.java   # Ownership CRUD
│   │   │   │
│   │   │   ├── dtos/                          # Data Transfer Objects
│   │   │   │   ├── requests/
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   └── DataGenerationRequest.java
│   │   │   │   └── responses/
│   │   │   │       ├── UserResponse.java
│   │   │   │       ├── ApiResponse.java
│   │   │   │       └── DataGenerationResponse.java
│   │   │   │
│   │   │   └── utils/                         # Utility classes
│   │   │       ├── EncryptionUtil.java        # AES-GCM encryption
│   │   │       └── PasswordService.java       # PBKDF2 hashing
│   │   │
│   │   └── resources/
│   │       └── application.yml                # Application configuration
│   │
│   └── test/
│       ├── java/com/videogamedb/
│       │   ├── services/
│       │   │   └── UserServiceTest.java       # Service unit tests
│       │   └── utils/
│       │       ├── EncryptionUtilTest.java    # Encryption tests
│       │       └── PasswordServiceTest.java   # Password tests
│       │
│       └── resources/
│           └── application-test.yml           # Test configuration
```

## 📊 Component Breakdown

### Configuration Layer (4 files)
- **SecurityConfig**: Session authentication, CSRF, cookie security
- **MongoStartupRunner**: Index creation, environment validation
- **GlobalExceptionHandler**: Centralized error handling

### Data Layer (12 models + 12 repositories)
- **Models**: MongoDB entity classes with proper annotations
- **Repositories**: Spring Data MongoDB interfaces with custom queries

### Service Layer (3 services)
- **UserService**: User management, authentication, encryption/decryption
- **DataGenerationService**: Python-compatible mock data generation
- **PasswordService**: PBKDF2 password hashing

### API Layer (8 controllers)
- **AuthController**: Registration, login, logout, profile
- **DataGeneratorController**: Mock data generation endpoints
- **6 Entity Controllers**: Full CRUD operations

### Security Layer (2 utilities)
- **EncryptionUtil**: AES-256-GCM field encryption, SHA-256 hashing
- **PasswordService**: PBKDF2-HMAC-SHA256 password hashing

### DTO Layer (6 DTOs)
- **Requests**: Input validation and structure
- **Responses**: Sanitized output without encrypted fields

## 🔢 Statistics

| Category | Count | Lines of Code (approx) |
|----------|-------|------------------------|
| Models | 12 | ~1,000 |
| Repositories | 12 | ~400 |
| Services | 3 | ~800 |
| Controllers | 8 | ~1,200 |
| Configuration | 4 | ~600 |
| Utilities | 2 | ~400 |
| DTOs | 6 | ~200 |
| Tests | 3 | ~400 |
| **Total** | **51 files** | **~5,000 LOC** |

## 🔐 Security Components

### Encryption Flow
```
User Input → EncryptionUtil.encryptField() → Base64(IV + Ciphertext + Tag) → MongoDB
MongoDB → Base64 String → EncryptionUtil.decryptField() → Plaintext → User Response
```

### Authentication Flow
```
Login Request → UserService.authenticateUser() 
    → Hash username (SHA-256)
    → Find user by hash
    → Verify password (PBKDF2)
    → Check account lock
    → Create session
    → Record access time
    → Return user response
```

### Data Generation Flow
```
POST /api/data/generate/all
    → DataGenerationService.generateAllData()
        → Generate platforms, genres, contributors
        → Generate games with relationships
        → Generate users with encrypted fields
        → Generate owned games, plays, ratings
        → Generate access times, follows, collections
    → Save to MongoDB
    → Return counts and sample credentials
```

## 📝 Key Files to Review

### For Understanding Security
1. `utils/EncryptionUtil.java` - AES-GCM implementation
2. `utils/PasswordService.java` - PBKDF2 implementation
3. `config/SecurityConfig.java` - Spring Security setup
4. `services/UserService.java` - Authentication logic

### For Understanding Data Model
1. `models/User.java` - Encrypted user entity
2. `models/VideoGame.java` - Game entity with relationships
3. `repositories/UserRepository.java` - Custom queries
4. `config/MongoStartupRunner.java` - Index definitions

### For Understanding API
1. `controllers/AuthController.java` - Authentication endpoints
2. `controllers/VideoGameController.java` - CRUD example
3. `dtos/responses/ApiResponse.java` - Response envelope
4. `config/GlobalExceptionHandler.java` - Error handling

### For Understanding Data Generation
1. `services/DataGenerationService.java` - Mock data logic
2. `controllers/DataGeneratorController.java` - Generation API

### For Testing
1. `test/.../EncryptionUtilTest.java` - Encryption tests
2. `test/.../PasswordServiceTest.java` - Hashing tests
3. `test/.../UserServiceTest.java` - Service tests

## 🚀 Quick Start Files

To get started, you need to:

1. **Set environment variables** (see `.env.example`)
2. **Review configuration** (`application.yml`)
3. **Run the application** (`VideoGameDbApplication.java`)
4. **Test with data generation** (`POST /api/data/generate/all`)
5. **Login with generated credentials** (`POST /api/auth/login`)

## 📖 Documentation Files

- **README.md**: Complete user guide with API documentation
- **IMPLEMENTATION_SUMMARY.md**: Technical implementation details
- **PROJECT_STRUCTURE.md**: This file - project organization
- **.env.example**: Environment configuration template

## 🔄 Dependency Flow

```
Controllers
    ↓ (use)
Services
    ↓ (use)
Repositories
    ↓ (access)
MongoDB

Utilities (EncryptionUtil, PasswordService)
    ↓ (injected into)
Services
    ↓ (used by)
Controllers
```

## 🎯 Entry Points

| Entry Point | Purpose |
|-------------|---------|
| `VideoGameDbApplication.main()` | Application startup |
| `MongoStartupRunner.run()` | Database initialization |
| `AuthController.login()` | User authentication |
| `DataGeneratorController.generateAll()` | Mock data creation |

---

**Navigate the codebase with confidence!** Each component is well-documented and follows Spring Boot best practices.
