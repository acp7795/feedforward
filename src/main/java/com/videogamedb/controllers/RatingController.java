package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Rating;
import com.videogamedb.repositories.RatingRepository;
import com.videogamedb.utils.LogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingRepository ratingRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Rating>>> getAllRatings() {
        log.info("📝 ENTER getAllRatings()");

        try {
            log.debug("🗂 Querying all ratings from database");
            List<Rating> ratings = ratingRepository.findAll();
            log.info("✅ Retrieved {} ratings", ratings.size());

            ApiResponse<List<Rating>> response = ApiResponse.success("Ratings retrieved", ratings);
            log.info("📝 EXIT getAllRatings() - count: {}", ratings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllRatings()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rating>> getRatingById(@PathVariable String id) {
        log.info("📝 ENTER getRatingById(id: {})", id);

        try {
            log.debug("🗂 Querying rating by ID: {}", id);
            Optional<Rating> rating = ratingRepository.findById(id);

            if (rating.isPresent()) {
                Rating found = rating.get();
                log.info("✅ Found rating: User {} rated Game {} with {} stars",
                        found.getUserId(), found.getGameId(), found.getRating());
                ApiResponse<Rating> response = ApiResponse.success("Rating found", found);
                log.info("📝 EXIT getRatingById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Rating not found for ID: {}", id);
                ApiResponse<Rating> response = ApiResponse.error("Rating not found");
                log.info("📝 EXIT getRatingById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getRatingById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getRatingsByUser(@PathVariable String userId) {
        log.info("📝 ENTER getRatingsByUser(userId: {})", userId);

        try {
            log.debug("🗂 Querying ratings by user: {}", userId);
            List<Rating> ratings = ratingRepository.findByUserId(userId);
            log.info("✅ User {} has given {} ratings", userId, ratings.size());

            ApiResponse<List<Rating>> response = ApiResponse.success("User ratings retrieved", ratings);
            log.info("📝 EXIT getRatingsByUser() - count: {}", ratings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getRatingsByUser() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getRatingsByGame(@PathVariable String gameId) {
        log.info("📝 ENTER getRatingsByGame(gameId: {})", gameId);

        try {
            log.debug("🗂 Querying ratings for game: {}", gameId);
            List<Rating> ratings = ratingRepository.findByGameId(gameId);
            log.info("✅ Game {} has {} ratings", gameId, ratings.size());

            ApiResponse<List<Rating>> response = ApiResponse.success("Game ratings retrieved", ratings);
            log.info("📝 EXIT getRatingsByGame() - count: {}", ratings.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getRatingsByGame() for game: {}", gameId, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<ApiResponse<Rating>> getRatingByUserAndGame(
            @PathVariable String userId,
            @PathVariable String gameId) {
        log.info("📝 ENTER getRatingByUserAndGame(userId: {}, gameId: {})", userId, gameId);

        try {
            log.debug("🗂 Querying rating for user {} and game {}", userId, gameId);
            Optional<Rating> rating = ratingRepository.findByUserIdAndGameId(userId, gameId);

            if (rating.isPresent()) {
                Rating found = rating.get();
                log.info("✅ Found rating: {} stars by user {} for game {}",
                        found.getRating(), userId, gameId);
                ApiResponse<Rating> response = ApiResponse.success("Rating found", found);
                log.info("📝 EXIT getRatingByUserAndGame() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Rating not found for user {} and game {}", userId, gameId);
                ApiResponse<Rating> response = ApiResponse.error("Rating not found");
                log.info("📝 EXIT getRatingByUserAndGame() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getRatingByUserAndGame() for user {} game {}", userId, gameId, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Rating>> createRating(@RequestBody Rating rating) {
        log.info("📝 ENTER createRating(userId: {}, gameId: {}, rating: {})",
                rating.getUserId(), rating.getGameId(), rating.getRating());

        try {
            log.debug("🔍 Checking if rating already exists");
            Optional<Rating> existing = ratingRepository.findByUserIdAndGameId(
                    rating.getUserId(), rating.getGameId());
            if (existing.isPresent()) {
                log.warn("❌ Rating already exists for user {} and game {}",
                        rating.getUserId(), rating.getGameId());
                ApiResponse<Rating> response = ApiResponse.error("Rating already exists for this user and game");
                log.info("📝 EXIT createRating() - conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            log.debug("🗂 Saving new rating");
            Rating saved = ratingRepository.save(rating);
            log.info("✅ Rating created: User {} rated Game {} with {} stars",
                    saved.getUserId(), saved.getGameId(), saved.getRating());

            ApiResponse<Rating> response = ApiResponse.success("Rating created", saved);
            log.info("📝 EXIT createRating() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createRating() for user {} game {}",
                    rating.getUserId(), rating.getGameId(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Rating>> updateRating(
            @PathVariable String id,
            @RequestBody Rating rating) {
        log.info("📝 ENTER updateRating(id: {}, userId: {}, gameId: {}, rating: {})",
                id, rating.getUserId(), rating.getGameId(), rating.getRating());

        try {
            log.debug("🔍 Checking if rating exists: {}", id);
            if (!ratingRepository.existsById(id)) {
                log.warn("❌ Rating not found for update: {}", id);
                ApiResponse<Rating> response = ApiResponse.error("Rating not found");
                log.info("📝 EXIT updateRating() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Updating rating with ID: {}", id);
            rating.setId(id);
            Rating updated = ratingRepository.save(rating);
            log.info("✅ Rating updated: {} (user: {}, game: {}, rating: {})",
                    id, updated.getUserId(), updated.getGameId(), updated.getRating());

            ApiResponse<Rating> response = ApiResponse.success("Rating updated", updated);
            log.info("📝 EXIT updateRating() - updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in updateRating() for ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRating(@PathVariable String id) {
        log.info("📝 ENTER deleteRating(id: {})", id);

        try {
            log.debug("🔍 Checking if rating exists: {}", id);
            if (!ratingRepository.existsById(id)) {
                log.warn("❌ Rating not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Rating not found");
                log.info("📝 EXIT deleteRating() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting rating: {}", id);
            ratingRepository.deleteById(id);
            log.info("✅ Rating deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Rating deleted", null);
            log.info("📝 EXIT deleteRating() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deleteRating() for ID: {}", id, e);
            throw e;
        }
    }
}