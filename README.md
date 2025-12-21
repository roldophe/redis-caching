# Redis Caching Demo

A Spring Boot application demonstrating Redis caching implementation for improved performance and scalability.

## Features

- User CRUD operations with Redis caching
- H2 in-memory database for data persistence
- Redis cache with TTL (Time To Live)
- Automatic cache invalidation on updates/deletes
- RESTful API endpoints
- Code formatting with Spotless (Google Java Format)

## Tech Stack

- Java 17
- Spring Boot 4.0.0
- Spring Data JPA
- Spring Data Redis
- Redis (Jedis client)
- H2 Database
- Lombok
- Gradle

## Prerequisites

- Java 17 or higher
- Redis Server running on localhost:6379
- Gradle (wrapper included)

## Redis Setup

### Install Redis

**macOS (using Homebrew):**
```bash
brew install redis
```

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install redis-server
```

**Windows:**
Download from [Redis for Windows](https://github.com/microsoftarchive/redis/releases)

### Start Redis Server

**macOS/Linux:**
```bash
redis-server
```

**Check if Redis is running:**
```bash
redis-cli ping
# Should return: PONG
```

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd redis-caching
```

### 2. Start Redis

Make sure Redis is running on `localhost:6379` (default configuration)

### 3. Run the application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 4. Access H2 Console (Optional)

URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Get All Users
```http
GET /api/users
```

### Get User by ID (Cached)
```http
GET /api/users/{id}
```
**Cache behavior:** First call fetches from DB, subsequent calls return from Redis cache

### Get User by Email (Cached)
```http
GET /api/users/email/{email}
```

### Create User
```http
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1-555-0101"
}
```
**Cache behavior:** Automatically caches the created user

### Update User
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "phone": "+1-555-0102"
}
```
**Cache behavior:** Updates the cache with new data

### Delete User
```http
DELETE /api/users/{id}
```
**Cache behavior:** Removes the user from cache

### Clear All Cache
```http
DELETE /api/users/cache/clear
```

## Redis Configuration

Configuration is in `src/main/resources/application.yaml`:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      # Add these if Redis requires authentication
      # password: your-password
      # username: default
      jedis:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms

  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes in milliseconds
```

### Cache Key Pattern

Cached keys follow this pattern:
```
{application-name}:{profile}:{cache-name}::{key}
```

Example:
```
redis-caching:default:users::1
redis-caching:default:users::john@example.com
```

## Viewing Cached Data

### Option 1: Redis CLI

```bash
# Connect to Redis
redis-cli

# View all keys
KEYS *

# Get a specific cached user
GET "redis-caching:default:users::1"

# View key type
TYPE "redis-caching:default:users::1"

# Check TTL (Time To Live)
TTL "redis-caching:default:users::1"

# Monitor all Redis commands in real-time
MONITOR

# Clear all cache
FLUSHALL

# Exit
exit
```

### Option 2: RedisInsight (GUI)

1. Install RedisInsight:
   ```bash
   # macOS
   brew install --cask redisinsight

   # Or download from: https://redis.io/insight/
   ```

2. Open RedisInsight and connect to `localhost:6379`

### Option 3: Another Redis Desktop Manager

```bash
brew install --cask another-redis-desktop-manager
```

## Caching Annotations Explained

### @Cacheable
Caches the method result. Subsequent calls with the same parameters return cached data.
```java
@Cacheable(value = "users", key = "#id")
public User getUserById(Long id) {
    // This executes only on cache miss
    return userRepository.findById(id).orElseThrow();
}
```

### @CachePut
Always executes the method and updates the cache with the result.
```java
@CachePut(value = "users", key = "#result.id")
public User createUser(User user) {
    return userRepository.save(user);
}
```

### @CacheEvict
Removes entries from the cache.
```java
@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}

@CacheEvict(value = "users", allEntries = true)
public void clearCache() {
    // Clears all entries in "users" cache
}
```

## Code Formatting

This project uses Spotless with Google Java Format.

### Apply formatting
```bash
./gradlew spotlessApply
```

### Check formatting
```bash
./gradlew spotlessCheck
```

## Testing Cache Behavior

### 1. Get a user (first call - DB query)
```bash
curl http://localhost:8080/api/users/1
```
Check logs: You'll see "Fetching user from database with id: 1"

### 2. Get the same user (second call - from cache)
```bash
curl http://localhost:8080/api/users/1
```
Check logs: No database query log (data served from Redis)

### 3. Monitor Redis
```bash
redis-cli MONITOR
```
Then make API calls to see Redis operations in real-time

### 4. View cached data
```bash
redis-cli KEYS "redis-caching:*"
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/redis_caching/
│   │   ├── config/
│   │   │   ├── DataInitializer.java     # Sample data loader
│   │   │   └── RedisConfig.java         # Redis configuration
│   │   ├── controller/
│   │   │   └── UserController.java      # REST endpoints
│   │   ├── entity/
│   │   │   └── User.java                # User entity
│   │   ├── repository/
│   │   │   └── UserRepository.java      # JPA repository
│   │   ├── service/
│   │   │   └── UserService.java         # Business logic with caching
│   │   └── RedisCachingApplication.java # Main application
│   └── resources/
│       └── application.yaml             # Application configuration
└── test/
    └── java/com/example/redis_caching/
        └── RedisCachingApplicationTests.java
```

## Troubleshooting

### Redis Connection Refused
**Problem:** `Connection refused: localhost:6379`

**Solution:**
```bash
# Check if Redis is running
redis-cli ping

# If not running, start Redis
redis-server
```

### Cache Not Working
**Problem:** Data always fetched from database

**Solution:**
1. Verify Redis is running: `redis-cli ping`
2. Check Redis keys: `redis-cli KEYS "*"`
3. Review application logs for cache errors
4. Verify `@EnableCaching` is present in configuration

### Port Already in Use
**Problem:** Redis port 6379 already in use

**Solution:**
```bash
# Find process using port 6379
lsof -i :6379

# Kill the process
kill -9 <PID>

# Or change port in application.yaml
spring:
  data:
    redis:
      port: 6380
```

## Performance Benefits

- **Reduced database load:** Frequently accessed data served from Redis
- **Lower latency:** Redis in-memory access (~1ms vs DB ~10-100ms)
- **Scalability:** Handles high read traffic efficiently
- **Cost savings:** Fewer database queries reduce infrastructure costs

## License

This project is for demonstration purposes.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Apply Spotless formatting: `./gradlew spotlessApply`
4. Commit your changes
5. Push to the branch
6. Create a Pull Request