package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.OwnedGame;
import com.videogamedb.repositories.OwnedGameRepository;
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
@RequestMapping("/api/owned-games")
@RequiredArgsConstructor
public class OwnedGameController {

    private final OwnedGameRepository ownedGameRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OwnedGame>>> getAllOwnedGames() {
        log.info("📝 ENTER getAllOwnedGames()");

        try {
            log.debug("🗂 Querying all owned games from database");
            List<OwnedGame> ownedGames = ownedGameRepository.findAll();
            log.info("✅ Retrieved {} owned game records", ownedGames.size());

            ApiResponse<List<OwnedGame>> response = ApiResponse.success("Owned games retrieved", ownedGames);
            log.info("📝 EXIT getAllOwnedGames() - count: {}", ownedGames.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllOwnedGames()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnedGame>> getOwnedGameById(@PathVariable String id) {
        log.info("📝 ENTER getOwnedGameById(id: {})", id);

        try {
            log.debug("🗂 Querying owned game by ID: {}", id);
            Optional<OwnedGame> ownedGame = ownedGameRepository.findById(id);

            if (ownedGame.isPresent()) {
                OwnedGame found = ownedGame.get();
                log.info("✅ Found owned game: User {} owns Game {}", found.getUserId(), found.getGameId());
                ApiResponse<OwnedGame> response = ApiResponse.success("Owned game found", found);
                log.info("📝 EXIT getOwnedGameById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Owned game not found for ID: {}", id);
                ApiResponse<OwnedGame> response = ApiResponse.error("Owned game not found");
                log.info("📝 EXIT getOwnedGameById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getOwnedGameById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OwnedGame>>> getOwnedGamesByUser(@PathVariable String userId) {
        log.info("📝 ENTER getOwnedGamesByUser(userId: {})", userId);

        try {
            log.debug("🗂 Querying owned games for user: {}", userId);
            List<OwnedGame> ownedGames = ownedGameRepository.findByUserId(userId);
            log.info("✅ User {} owns {} games", userId, ownedGames.size());

            ApiResponse<List<OwnedGame>> response = ApiResponse.success("User's owned games retrieved", ownedGames);
            log.info("📝 EXIT getOwnedGamesByUser() - count: {}", ownedGames.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getOwnedGamesByUser() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<List<OwnedGame>>> getOwnedGamesByGame(@PathVariable String gameId) {
        log.info("📝 ENTER getOwnedGamesByGame(gameId: {})", gameId);

        try {
            log.debug("🗂 Querying ownership records for game: {}", gameId);
            List<OwnedGame> ownedGames = ownedGameRepository.findByGameId(gameId);
            log.info("✅ Game {} is owned by {} users", gameId, ownedGames.size());

            ApiResponse<List<OwnedGame>> response = ApiResponse.success("Game ownership records retrieved", ownedGames);
            log.info("📝 EXIT getOwnedGamesByGame() - count: {}", ownedGames.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getOwnedGamesByGame() for game: {}", gameId, e);
            throw e;
        }
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<ApiResponse<Long>> getOwnedGamesCountByUser(@PathVariable String userId) {
        log.info("📝 ENTER getOwnedGamesCountByUser(userId: {})", userId);

        try {
            log.debug("🗂 Counting owned games for user: {}", userId);
            long count = ownedGameRepository.countByUserId(userId);
            log.info("✅ User {} owns {} games", userId, count);

            ApiResponse<Long> response = ApiResponse.success("Owned games count retrieved", count);
            log.info("📝 EXIT getOwnedGamesCountByUser() - count: {}", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getOwnedGamesCountByUser() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/count/game/{gameId}")
    public ResponseEntity<ApiResponse<Long>> getOwnedGamesCountByGame(@PathVariable String gameId) {
        log.info("📝 ENTER getOwnedGamesCountByGame(gameId: {})", gameId);

        try {
            log.debug("🗂 Counting owners for game: {}", gameId);
            long count = ownedGameRepository.countByGameId(gameId);
            log.info("✅ Game {} has {} owners", gameId, count);

            ApiResponse<Long> response = ApiResponse.success("Game ownership count retrieved", count);
            log.info("📝 EXIT getOwnedGamesCountByGame() - count: {}", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getOwnedGamesCountByGame() for game: {}", gameId, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OwnedGame>> createOwnedGame(@RequestBody OwnedGame ownedGame) {
        log.info("📝 ENTER createOwnedGame(userId: {}, gameId: {})",
                ownedGame.getUserId(), ownedGame.getGameId());

        try {
            log.debug("🔍 Checking if user already owns this game");
            if (ownedGameRepository.existsByUserIdAndGameId(
                    ownedGame.getUserId(), ownedGame.getGameId())) {
                log.warn("❌ User {} already owns game {}", ownedGame.getUserId(), ownedGame.getGameId());
                ApiResponse<OwnedGame> response = ApiResponse.error("User already owns this game");
                log.info("📝 EXIT createOwnedGame() - conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            log.debug("🗂 Saving new owned game record");
            OwnedGame saved = ownedGameRepository.save(ownedGame);
            log.info("✅ Owned game created: User {} owns game {}", saved.getUserId(), saved.getGameId());

            ApiResponse<OwnedGame> response = ApiResponse.success("Owned game created", saved);
            log.info("📝 EXIT createOwnedGame() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createOwnedGame() for user {} game {}",
                    ownedGame.getUserId(), ownedGame.getGameId(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOwnedGame(@PathVariable String id) {
        log.info("📝 ENTER deleteOwnedGame(id: {})", id);

        try {
            log.debug("🔍 Checking if owned game exists: {}", id);
            if (!ownedGameRepository.existsById(id)) {
                log.warn("❌ Owned game not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Owned game not found");
                log.info("📝 EXIT deleteOwnedGame() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting owned game: {}", id);
            ownedGameRepository.deleteById(id);
            log.info("✅ Owned game deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Owned game deleted", null);
            log.info("📝 EXIT deleteOwnedGame() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deleteOwnedGame() for ID: {}", id, e);
            throw e;
        }
    }
}