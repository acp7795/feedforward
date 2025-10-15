# VideoGameDB Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Generate Encryption Key

```bash
# Using OpenSSL
openssl rand -base64 32

# Or using Python
python3 -c "import base64; import os; print(base64.b64encode(os.urandom(32)).decode())"

# Example output:
# 7v8y/B?E(H+MbQeThWmZq4t7w!z%C*F-
```

Copy the output for the next step.

### Step 2: Set Environment Variables

```bash
# Create .env file
cat > .env << 'EOF'
export MONGO_URI="mongodb://localhost:27017/videogamedb"
export ENCRYPTION_KEY="<paste-your-key-here>"
export COOKIE_SECURE="false"
EOF

# Load environment
source .env
```

### Step 3: Start MongoDB

```bash
# Using Docker
docker run -d -p 27017:27017 --name videogamedb-mongo mongo:7.0

# Or use your local MongoDB installation
```

### Step 4: Run the Application

```bash
# Build and run
mvn spring-boot:run

# Wait for this message:
# ✅ MongoDB initialization complete
```

### Step 5: Generate Test Data

```bash
# Generate complete dataset (50 users, 30 games, etc.)
curl -X POST http://localhost:8080/api/data/generate/all

# Response includes sample credentials:
# {
#   "status": "success",
#   "data": {
#     "counts": { "users": 50, "videogames": 30, ... },
#     "sampleData": {
#       "sampleUsername": "john_doe",
#       "samplePassword": "generatedPassword123"
#     }
#   }
# }
```

Save the sample username and password from the response!

### Step 6: Login

```bash
# Login with generated credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "generatedPassword123"
  }' \
  -c cookies.txt

# Response:
# {
#   "status": "success",
#   "message": "Login successful",
#   "data": { ... user profile ... }
# }
```

### Step 7: Make Authenticated Requests

```bash
# Get current user profile
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt

# List all games (paginated)
curl -X GET "http://localhost:8080/api/games?page=0&size=10" \
  -b cookies.txt

# Search games by title
curl -X GET "http://localhost:8080/api/games/search?title=adventure" \
  -b cookies.txt

# Get user's owned games
curl -X GET "http://localhost:8080/api/owned-games/user/<userId>" \
  -b cookies.txt

# Get user's ratings
curl -X GET "http://localhost:8080/api/ratings/user/<userId>" \
  -b cookies.txt
```

## 📖 Common Operations

### Register New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "SecurePassword123!",
    "firstName": "John",
    "lastName": "Doe",
    "platforms": []
  }'
```

### Create a Game

```bash
curl -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "title": "My Awesome Game",
    "esrb": "T",
    "developers": ["<developerId>"],
    "publishers": ["<publisherId>"],
    "genres": ["<genreId>"]
  }'
```

### Add Rating

```bash
curl -X POST http://localhost:8080/api/ratings \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "userId": "<userId>",
    "gameId": "<gameId>",
    "rating": 5,
    "ratingDate": "2025-10-15T12:00:00"
  }'
```

### Create Collection

```bash
curl -X POST http://localhost:8080/api/collections \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "My Favorites",
    "description": "Games I love",
    "userId": "<userId>",
    "games": ["<gameId1>", "<gameId2>"]
  }'
```

### Follow Another User

```bash
curl -X POST http://localhost:8080/api/follows \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "followerId": "<yourUserId>",
    "followedId": "<otherUserId>"
  }'
```

### Record Play Session

```bash
curl -X POST http://localhost:8080/api/play-sessions \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "userId": "<userId>",
    "gameId": "<gameId>",
    "datetimeOpened": "2025-10-15T10:00:00",
    "timePlayed": 7200
  }'
```

## 🔍 Query Examples

### Search Games

```bash
# By title
GET /api/games/search?title=mario

# By ESRB rating
GET /api/games/esrb/T

# By genre
GET /api/games/genre/<genreId>
```

### User Queries

```bash
# User's owned games count
GET /api/owned-games/count/user/<userId>

# User's followers
GET /api/follows/followers/<userId>

# User's following
GET /api/follows/following/<userId>

# User's collections
GET /api/collections/user/<userId>

# User's play sessions
GET /api/play-sessions/user/<userId>
```

### Time-Based Queries

```bash
# Access times in range
GET /api/access-times/between?start=2025-01-01T00:00:00&end=2025-12-31T23:59:59

# Play sessions in range
GET /api/play-sessions/between?start=2025-01-01T00:00:00&end=2025-12-31T23:59:59

# Access times after date
GET /api/access-times/after/2025-10-01T00:00:00
```

## 📊 View API Documentation

Open in browser:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🐞 Troubleshooting

### "ENCRYPTION_KEY is not set"

```bash
# Make sure you've set the environment variable
echo $ENCRYPTION_KEY

# If empty, load your .env file
source .env
```

### "Could not connect to MongoDB"

```bash
# Check if MongoDB is running
docker ps | grep mongo

# Or check local MongoDB
mongosh --eval "db.version()"

# Start MongoDB if needed
docker start videogamedb-mongo
```

### "Session expired" / "Not authenticated"

```bash
# Login again to get a fresh session
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username","password":"your_password"}' \
  -c cookies.txt
```

### Application won't start

```bash
# Check Java version (needs Java 17+)
java -version

# Check if port 8080 is available
lsof -i :8080

# View application logs
tail -f logs/application.log
```

## 💡 Tips

### Use jq for Pretty JSON

```bash
# Install jq
sudo apt-get install jq  # Ubuntu/Debian
brew install jq          # macOS

# Pretty print responses
curl -X GET http://localhost:8080/api/games | jq '.'
```

### Save Session for Reuse

```bash
# Login once and save cookies
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass"}' \
  -c ~/.videogamedb-session

# Use saved session
curl -X GET http://localhost:8080/api/auth/me \
  -b ~/.videogamedb-session
```

### Generate Different Dataset Sizes

```bash
# Small dataset (10 users, 5 games)
curl -X POST http://localhost:8080/api/data/generate \
  -H "Content-Type: application/json" \
  -d '{
    "numUsers": 10,
    "numGames": 5,
    "numContributors": 10,
    "numPlatforms": 3,
    "numGenres": 5,
    "saveToDb": true
  }'

# Large dataset (200 users, 100 games)
curl -X POST http://localhost:8080/api/data/generate \
  -H "Content-Type: application/json" \
  -d '{
    "numUsers": 200,
    "numGames": 100,
    "numContributors": 80,
    "numPlatforms": 8,
    "numGenres": 15,
    "saveToDb": true
  }'
```

## 🔐 Security Notes

1. **Never commit .env file** - It's in .gitignore
2. **Use HTTPS in production** - Set COOKIE_SECURE=true
3. **Rotate encryption keys regularly** - Plan for re-encryption
4. **Monitor failed login attempts** - Check logs for patterns
5. **Disable data generation in production** - Or add authentication

## 📚 Next Steps

- Read [README.md](README.md) for comprehensive documentation
- Review [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for technical details
- Check [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) for codebase organization
- Explore Swagger UI for interactive API testing

## 🎯 Quick Test Checklist

- [ ] MongoDB is running
- [ ] Environment variables are set
- [ ] Application starts successfully
- [ ] Can generate test data
- [ ] Can register a new user
- [ ] Can login with generated credentials
- [ ] Can view games list
- [ ] Can create a rating
- [ ] Can view own profile
- [ ] Can logout successfully

---

**You're ready to build on VideoGameDB!** 🎮
