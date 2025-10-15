package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Rating;
import com.videogamedb.repositories.RatingRepository;
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
        List<Rating> ratings = ratingRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Ratings retrieved", ratings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rating>> getRatingById(@PathVariable String id) {
        Optional<Rating> rating = ratingRepository.findById(id);
        return rating
            .map(r -> ResponseEntity.ok(ApiResponse.success("Rating found", r)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Rating not found")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getRatingsByUser(@PathVariable String userId) {
        List<Rating> ratings = ratingRepository.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User ratings retrieved", ratings));
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getRatingsByGame(@PathVariable String gameId) {
        List<Rating> ratings = ratingRepository.findByGameId(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game ratings retrieved", ratings));
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<ApiResponse<Rating>> getRatingByUserAndGame(
            @PathVariable String userId,
            @PathVariable String gameId) {
        Optional<Rating> rating = ratingRepository.findByUserIdAndGameId(userId, gameId);
        return rating
            .map(r -> ResponseEntity.ok(ApiResponse.success("Rating found", r)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Rating not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Rating>> createRating(@RequestBody Rating rating) {
        // Check if rating already exists
        Optional<Rating> existing = ratingRepository.findByUserIdAndGameId(
            rating.getUserId(), rating.getGameId()
        );
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Rating already exists for this user and game"));
        }

        Rating saved = ratingRepository.save(rating);
        log.info("✅ Rating created: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Rating created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Rating>> updateRating(
            @PathVariable String id,
            @RequestBody Rating rating) {
        if (!ratingRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Rating not found"));
        }
        rating.setId(id);
        Rating updated = ratingRepository.save(rating);
        log.info("✅ Rating updated: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Rating updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRating(@PathVariable String id) {
        if (!ratingRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Rating not found"));
        }
        ratingRepository.deleteById(id);
        log.info("✅ Rating deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Rating deleted", null));
    }
}
