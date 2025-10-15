package com.videogamedb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Startup runner to validate environment and create MongoDB indexes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoStartupRunner implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Value("${app.encryption.key}")
    private String encryptionKey;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Starting MongoDB initialization...");

        // Validate required environment variables
        validateEnvironmentVariables();

        // Create indexes
        createIndexes();

        log.info("✅ MongoDB initialization complete");
    }

    private void validateEnvironmentVariables() {
        if (encryptionKey == null || encryptionKey.isEmpty()) {
            log.error("❌ ENCRYPTION_KEY is not set. Application cannot start without encryption key.");
            throw new IllegalStateException("ENCRYPTION_KEY environment variable is required but not set");
        }

        if (mongoUri == null || mongoUri.isEmpty()) {
            log.error("❌ MONGO_URI is not set.");
            throw new IllegalStateException("MONGO_URI environment variable is required but not set");
        }

        log.info("✅ Environment variables validated successfully");
    }

    private void createIndexes() {
        log.info("📋 Creating MongoDB indexes...");

        // Users collection indexes
        createUserIndexes();

        // Video games collection indexes
        createVideoGameIndexes();

        // Platform releases indexes
        createPlatformReleaseIndexes();

        // Owned games indexes
        createOwnedGamesIndexes();

        // Play sessions indexes
        createPlaySessionIndexes();

        // Ratings indexes
        createRatingIndexes();

        // Access times indexes
        createAccessTimeIndexes();

        // Follows indexes
        createFollowIndexes();

        // Collections indexes
        createCollectionIndexes();

        // Platforms and genres indexes
        createPlatformGenreIndexes();

        // Contributors indexes
        createContributorIndexes();

        log.info("✅ All indexes created successfully");
    }

    private void createUserIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("users");

        // Unique indexes
        indexOps.ensureIndex(new Index().on("username_enc", Sort.Direction.ASC).unique());
        indexOps.ensureIndex(new Index().on("username_hash", Sort.Direction.ASC).unique());
        indexOps.ensureIndex(new Index().on("email_enc", Sort.Direction.ASC).unique());

        // Regular indexes
        indexOps.ensureIndex(new Index().on("role", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("creationDate", Sort.Direction.DESC));
        indexOps.ensureIndex(new Index().on("account_locked_until", Sort.Direction.ASC));

        log.debug("✅ User indexes created");
    }

    private void createVideoGameIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("videogames");

        indexOps.ensureIndex(new Index().on("title", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("esrb", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("developers", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("publishers", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("genres", Sort.Direction.ASC));

        log.debug("✅ VideoGame indexes created");
    }

    private void createPlatformReleaseIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("platformReleases");

        indexOps.ensureIndex(new Index().on("game_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("platform_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("price", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("releaseDate", Sort.Direction.DESC));

        // Compound index for game + platform
        indexOps.ensureIndex(new Index()
            .on("game_id", Sort.Direction.ASC)
            .on("platform_id", Sort.Direction.ASC)
            .unique());

        log.debug("✅ PlatformRelease indexes created");
    }

    private void createOwnedGamesIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("owned");

        indexOps.ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("game_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("acquisitionDate", Sort.Direction.DESC));

        // Compound unique index
        indexOps.ensureIndex(new Index()
            .on("user_id", Sort.Direction.ASC)
            .on("game_id", Sort.Direction.ASC)
            .unique());

        log.debug("✅ OwnedGame indexes created");
    }

    private void createPlaySessionIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("plays");

        indexOps.ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("game_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("datetimeOpened", Sort.Direction.DESC));

        // Compound index for queries
        indexOps.ensureIndex(new Index()
            .on("user_id", Sort.Direction.ASC)
            .on("game_id", Sort.Direction.ASC)
            .on("datetimeOpened", Sort.Direction.DESC));

        log.debug("✅ PlaySession indexes created");
    }

    private void createRatingIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("ratings");

        indexOps.ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("game_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("rating", Sort.Direction.DESC));
        indexOps.ensureIndex(new Index().on("ratingDate", Sort.Direction.DESC));

        // Compound unique index
        indexOps.ensureIndex(new Index()
            .on("user_id", Sort.Direction.ASC)
            .on("game_id", Sort.Direction.ASC)
            .unique());

        log.debug("✅ Rating indexes created");
    }

    private void createAccessTimeIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("accessTimes");

        indexOps.ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("time", Sort.Direction.DESC));

        // Compound index
        indexOps.ensureIndex(new Index()
            .on("user_id", Sort.Direction.ASC)
            .on("time", Sort.Direction.DESC));

        log.debug("✅ AccessTime indexes created");
    }

    private void createFollowIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("follows");

        indexOps.ensureIndex(new Index().on("follower_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("followed_id", Sort.Direction.ASC));

        // Compound unique index
        indexOps.ensureIndex(new Index()
            .on("follower_id", Sort.Direction.ASC)
            .on("followed_id", Sort.Direction.ASC)
            .unique());

        log.debug("✅ Follow indexes created");
    }

    private void createCollectionIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("collections");

        indexOps.ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("name", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("games", Sort.Direction.ASC));

        log.debug("✅ Collection indexes created");
    }

    private void createPlatformGenreIndexes() {
        IndexOperations platformOps = mongoTemplate.indexOps("platforms");
        platformOps.ensureIndex(new Index().on("platform_name", Sort.Direction.ASC).unique());

        IndexOperations genreOps = mongoTemplate.indexOps("genres");
        genreOps.ensureIndex(new Index().on("genre_name", Sort.Direction.ASC).unique());

        log.debug("✅ Platform and Genre indexes created");
    }

    private void createContributorIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("contributors");

        indexOps.ensureIndex(new Index().on("contributor_name", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("type", Sort.Direction.ASC));

        log.debug("✅ Contributor indexes created");
    }
}
