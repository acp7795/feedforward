# VideoGameDB Spring Boot REST API

A secure, enterprise-grade REST API for managing video games, users, platforms, collections, ratings, and social interactions. Built with Spring Boot 3.x and MongoDB.

## 🎯 Features

- **Enterprise Security**
  - Server-side session authentication (no JWT)
  - PBKDF2-SHA256 password hashing (310,000 iterations)
  - AES-256-GCM field-level encryption for PII
  - Account lockout after failed login attempts
  - Secure HttpOnly cookies with SameSite protection

- **Comprehensive Data Management**
  - Full CRUD operations for all entities
  - Rich query endpoints with filtering and pagination
  - Normalized MongoDB schema with indexed collections
  - Mock data generator for testing and development

- **Python Compatibility**
  - Translated Python generator logic to Java
  - Compatible encryption/hashing behaviors
  - Identical MongoDB document schema

## 🏗️ Architecture

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Framework   | Spring Boot 3.2.0                   |
| Database    | MongoDB                             |
| Security    | Spring Security + Session MongoDB   |
| Data Access | Spring Data MongoDB                 |
| Validation  | Jakarta Validation                  |
| Testing     | JUnit 5, Mockito                    |
| API Docs    | OpenAPI 3 (springdoc-openapi)       |
| Mock Data   | JavaFaker                           |

## 📋 Prerequisites

- Java 17 or higher
- MongoDB 4.4 or higher
- Maven 3.6 or higher

## 🚀 Quick Start

### 1. Clone the repository

```bash
git clone <repository-url>
cd videogamedb-api
```

### 2. Set environment variables

Create a `.env` file in the project root:

```bash
# MongoDB connection
export MONGO_URI="mongodb://localhost:27017/videogamedb"

# Encryption key (32 bytes, base64 encoded)
export ENCRYPTION_KEY="your-base64-encoded-32-byte-key"

# Optional: Cookie security (set to true in production)
export COOKIE_SECURE="false"
```

**Generate an encryption key:**

```bash
# Using OpenSSL
openssl rand -base64 32

# Using Python
python3 -c "import base64; import os; print(base64.b64encode(os.urandom(32)).decode())"
```

### 3. Build the project

```bash
mvn clean install
```

### 4. Run the application

```bash
# Load environment variables
source .env

# Run the application
mvn spring-boot:run
```

The API will start at `http://localhost:8080`

### 5. Access API documentation

Open your browser and navigate to:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## 🗄️ Database Collections

The application creates and manages the following MongoDB collections:

| Collection         | Description                          |
|--------------------|--------------------------------------|
| `users`            | User accounts (encrypted PII)        |
| `videogames`       | Video game catalog                   |
| `platforms`        | Gaming platforms                     |
| `genres`           | Game genres                          |
| `contributors`     | Developers and publishers            |
| `platformReleases` | Game releases per platform           |
| `owned`            | User game ownership                  |
| `plays`            | Play session tracking                |
| `ratings`          | User game ratings                    |
| `accessTimes`      | User access timestamps               |
| `follows`          | User follow relationships            |
| `collections`      | User-created game collections        |
| `sessions`         | Spring session storage               |

## 🔐 Security Features

### Password Hashing

- Algorithm: PBKDF2-HMAC-SHA256
- Iterations: 310,000 (configurable)
- Salt: 16 bytes (random per password)
- Derived key: 32 bytes

### Field-Level Encryption

- Algorithm: AES-256-GCM
- IV: 12 bytes (random per encryption)
- Tag: 128 bits
- Format: Base64(IV || ciphertext || tag)

### Encrypted User Fields

- `username_enc` - Encrypted username
- `email_enc` - Encrypted email
- `firstName_enc` - Encrypted first name
- `lastName_enc` - Encrypted last name

### Searchable Fields

- `username_hash` - SHA-256 hash for lookups
- `email_masked` - Partially masked email

### Account Lockout

- Default: 5 failed attempts
- Lock duration: 30 minutes (configurable)
- Automatic unlock after duration

## 📡 API Endpoints

### Authentication

```http
POST   /api/auth/register      # Register new user
POST   /api/auth/login         # Login and create session
POST   /api/auth/logout        # Logout and invalidate session
GET    /api/auth/me            # Get current user profile
```

### Data Generation

```http
POST   /api/data/generate      # Generate custom dataset
POST   /api/data/generate/users?count=50
POST   /api/data/generate/games?count=30
POST   /api/data/generate/all  # Generate full dataset
```

### Games

```http
GET    /api/games              # List all games (paginated)
GET    /api/games/{id}         # Get game by ID
GET    /api/games/search?title={query}
GET    /api/games/esrb/{rating}
GET    /api/games/genre/{genreId}
POST   /api/games              # Create game
PUT    /api/games/{id}         # Update game
DELETE /api/games/{id}         # Delete game
```

### Platforms

```http
GET    /api/platforms          # List all platforms
GET    /api/platforms/{id}     # Get platform by ID
GET    /api/platforms/name/{name}
POST   /api/platforms          # Create platform
PUT    /api/platforms/{id}     # Update platform
DELETE /api/platforms/{id}     # Delete platform
```

### Ratings

```http
GET    /api/ratings            # List all ratings
GET    /api/ratings/{id}       # Get rating by ID
GET    /api/ratings/user/{userId}
GET    /api/ratings/game/{gameId}
GET    /api/ratings/user/{userId}/game/{gameId}
POST   /api/ratings            # Create rating
PUT    /api/ratings/{id}       # Update rating
DELETE /api/ratings/{id}       # Delete rating
```

### Play Sessions

```http
GET    /api/play-sessions      # List all sessions
GET    /api/play-sessions/{id} # Get session by ID
GET    /api/play-sessions/user/{userId}
GET    /api/play-sessions/game/{gameId}
GET    /api/play-sessions/between?start={datetime}&end={datetime}
POST   /api/play-sessions      # Create session
DELETE /api/play-sessions/{id} # Delete session
```

### Collections

```http
GET    /api/collections        # List all collections
GET    /api/collections/{id}   # Get collection by ID
GET    /api/collections/user/{userId}
GET    /api/collections/search?name={query}
POST   /api/collections        # Create collection
PUT    /api/collections/{id}   # Update collection
DELETE /api/collections/{id}   # Delete collection
```

### Follows

```http
GET    /api/follows            # List all follows
GET    /api/follows/followers/{userId}
GET    /api/follows/following/{userId}
GET    /api/follows/count/followers/{userId}
GET    /api/follows/count/following/{userId}
POST   /api/follows            # Create follow relationship
DELETE /api/follows/{id}       # Delete follow
DELETE /api/follows/unfollow?followerId={id}&followedId={id}
```

### Owned Games

```http
GET    /api/owned-games        # List all owned games
GET    /api/owned-games/user/{userId}
GET    /api/owned-games/game/{gameId}
GET    /api/owned-games/count/user/{userId}
GET    /api/owned-games/count/game/{gameId}
POST   /api/owned-games        # Add owned game
DELETE /api/owned-games/{id}   # Remove owned game
```

### Access Times

```http
GET    /api/access-times       # List all access times
GET    /api/access-times/user/{userId}
GET    /api/access-times/after/{datetime}
GET    /api/access-times/between?start={datetime}&end={datetime}
POST   /api/access-times       # Record access time
DELETE /api/access-times/{id}  # Delete access time
```

## 🎲 Data Generation

Generate mock data for testing:

### Generate Full Dataset

```bash
curl -X POST http://localhost:8080/api/data/generate/all
```

### Custom Generation

```bash
curl -X POST http://localhost:8080/api/data/generate \
  -H "Content-Type: application/json" \
  -d '{
    "datasetType": "all",
    "numUsers": 100,
    "numGames": 50,
    "numContributors": 60,
    "numPlatforms": 8,
    "numGenres": 15,
    "encryptFields": true,
    "hashPasswords": true,
    "saveToDb": true
  }'
```

The response includes:
- Counts of generated records
- Sample user credentials for testing
- Generation status

## 🧪 Testing

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=EncryptionUtilTest
mvn test -Dtest=PasswordServiceTest
mvn test -Dtest=UserServiceTest
```

### Test Coverage

- Unit tests for encryption and password utilities
- Unit tests for service layer
- Integration tests for authentication flow
- Mock data generation tests

## 🐳 Docker Deployment

### Build Docker image

```bash
docker build -t videogamedb-api:latest .
```

### Run with Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:7.0
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: videogamedb

  api:
    image: videogamedb-api:latest
    ports:
      - "8080:8080"
    environment:
      MONGO_URI: mongodb://mongodb:27017/videogamedb
      ENCRYPTION_KEY: ${ENCRYPTION_KEY}
      COOKIE_SECURE: "true"
    depends_on:
      - mongodb

volumes:
  mongodb_data:
```

Run:

```bash
docker-compose up -d
```

## ⚙️ Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
app:
  security:
    pbkdf2-iterations: 310000      # PBKDF2 iterations
    max-login-attempts: 5          # Login attempts before lock
    account-lock-minutes: 30       # Lock duration
  rate-limit:
    auth-requests-per-minute: 10   # Auth endpoint rate limit
```

### Environment Variables

| Variable         | Required | Default | Description                 |
|------------------|----------|---------|----------------------------|
| `MONGO_URI`      | Yes      | -       | MongoDB connection string  |
| `ENCRYPTION_KEY` | Yes      | -       | Base64 32-byte AES key     |
| `COOKIE_SECURE`  | No       | false   | Enable secure cookies      |

## 🔍 Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Application Logs

Logs include:
- Authentication events
- Failed login attempts
- Account lockouts
- Data generation operations
- Security events

**Note:** Sensitive data (passwords, encryption keys, PII) is never logged.

## 📊 Database Indexes

Indexes are automatically created on startup:

- **Users**: `username_enc`, `username_hash`, `email_enc`, `role`, `creationDate`
- **Games**: `title`, `esrb`, `developers`, `publishers`, `genres`
- **Ratings**: `user_id`, `game_id`, `rating`, compound(`user_id`, `game_id`)
- **Plays**: `user_id`, `game_id`, `datetimeOpened`
- **Follows**: compound(`follower_id`, `followed_id`)
- And more...

## 🛡️ Security Best Practices

1. **Always use HTTPS in production** - Set `COOKIE_SECURE=true`
2. **Rotate encryption keys** - Implement key rotation strategy
3. **Monitor failed login attempts** - Set up alerts
4. **Use strong MongoDB credentials** - Enable authentication
5. **Restrict data generation endpoints** - Disable in production or add auth
6. **Keep dependencies updated** - Run `mvn versions:display-dependency-updates`
7. **Enable MongoDB encryption at rest** - Use MongoDB Enterprise features
8. **Implement rate limiting** - Use Redis or similar for distributed rate limiting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Translated from original Python implementation
- Uses JavaFaker for realistic mock data generation
- Compatible with Python cryptography library patterns

## 📞 Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Check existing documentation
- Review API documentation at `/swagger-ui.html`

## 🔄 Version History

### v1.0.0 (Current)
- Initial Spring Boot implementation
- Python generator compatibility
- Full CRUD operations
- Session-based authentication
- Field-level encryption
- Mock data generation

---

Built with ❤️ using Spring Boot and MongoDB
