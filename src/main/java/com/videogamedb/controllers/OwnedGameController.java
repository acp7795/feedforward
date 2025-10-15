package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.OwnedGame;
import com.videogamedb.repositories.OwnedGameRepository;
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
        List<OwnedGame> ownedGames = ownedGameRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Owned games retrieved", ownedGames));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OwnedGame>> getOwnedGameById(@PathVariable String id) {
        Optional<OwnedGame> ownedGame = ownedGameRepository.findById(id);
        return ownedGame
            .map(og -> ResponseEntity.ok(ApiResponse.success("Owned game found", og)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Owned game not found")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OwnedGame>>> getOwnedGamesByUser(@PathVariable String userId) {
        List<OwnedGame> ownedGames = ownedGameRepository.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User's owned games retrieved", ownedGames));
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<List<OwnedGame>>> getOwnedGamesByGame(@PathVariable String gameId) {
        List<OwnedGame> ownedGames = ownedGameRepository.findByGameId(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game ownership records retrieved", ownedGames));
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<ApiResponse<Long>> getOwnedGamesCountByUser(@PathVariable String userId) {
        long count = ownedGameRepository.countByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Owned games count retrieved", count));
    }

    @GetMapping("/count/game/{gameId}")
    public ResponseEntity<ApiResponse<Long>> getOwnedGamesCountByGame(@PathVariable String gameId) {
        long count = ownedGameRepository.countByGameId(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game ownership count retrieved", count));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OwnedGame>> createOwnedGame(@RequestBody OwnedGame ownedGame) {
        // Check if already owned
        if (ownedGameRepository.existsByUserIdAndGameId(
                ownedGame.getUserId(), ownedGame.getGameId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("User already owns this game"));
        }

        OwnedGame saved = ownedGameRepository.save(ownedGame);
        log.info("✅ Owned game created: User {} owns game {}", saved.getUserId(), saved.getGameId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Owned game created", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOwnedGame(@PathVariable String id) {
        if (!ownedGameRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Owned game not found"));
        }
        ownedGameRepository.deleteById(id);
        log.info("✅ Owned game deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Owned game deleted", null));
    }
}
