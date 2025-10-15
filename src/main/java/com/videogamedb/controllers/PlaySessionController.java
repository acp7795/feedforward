package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.PlaySession;
import com.videogamedb.repositories.PlaySessionRepository;
import com.videogamedb.utils.LogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/play-sessions")
@RequiredArgsConstructor
public class PlaySessionController {

    private final PlaySessionRepository playSessionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlaySession>>> getAllPlaySessions() {
        log.info("📝 ENTER getAllPlaySessions()");

        try {
            log.debug("🗂 Querying all play sessions from database");
            List<PlaySession> sessions = playSessionRepository.findAll();
            log.info("✅ Retrieved {} play sessions", sessions.size());

            ApiResponse<List<PlaySession>> response = ApiResponse.success("Play sessions retrieved", sessions);
            log.info("📝 EXIT getAllPlaySessions() - count: {}", sessions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllPlaySessions()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaySession>> getPlaySessionById(@PathVariable String id) {
        log.info("📝 ENTER getPlaySessionById(id: {})", id);

        try {
            log.debug("🗂 Querying play session by ID: {}", id);
            Optional<PlaySession> session = playSessionRepository.findById(id);

            if (session.isPresent()) {
                PlaySession found = session.get();
                log.info("✅ Found play session: User {} playing Game {}",
                        found.getUserId(), found.getGameId());
                ApiResponse<PlaySession> response = ApiResponse.success("Play session found", found);
                log.info("📝 EXIT getPlaySessionById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Play session not found for ID: {}", id);
                ApiResponse<PlaySession> response = ApiResponse.error("Play session not found");
                log.info("📝 EXIT getPlaySessionById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getPlaySessionById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PlaySession>>> getPlaySessionsByUser(@PathVariable String userId) {
        log.info("📝 ENTER getPlaySessionsByUser(userId: {})", userId);

        try {
            log.debug("🗂 Querying play sessions for user: {}", userId);
            List<PlaySession> sessions = playSessionRepository.findByUserIdOrderByDatetimeOpenedDesc(userId);
            log.info("✅ Retrieved {} play sessions for user: {}", sessions.size(), userId);

            ApiResponse<List<PlaySession>> response = ApiResponse.success("User play sessions retrieved", sessions);
            log.info("📝 EXIT getPlaySessionsByUser() - count: {}", sessions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getPlaySessionsByUser() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<List<PlaySession>>> getPlaySessionsByGame(@PathVariable String gameId) {
        log.info("📝 ENTER getPlaySessionsByGame(gameId: {})", gameId);

        try {
            log.debug("🗂 Querying play sessions for game: {}", gameId);
            List<PlaySession> sessions = playSessionRepository.findByGameId(gameId);
            log.info("✅ Retrieved {} play sessions for game: {}", sessions.size(), gameId);

            ApiResponse<List<PlaySession>> response = ApiResponse.success("Game play sessions retrieved", sessions);
            log.info("📝 EXIT getPlaySessionsByGame() - count: {}", sessions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getPlaySessionsByGame() for game: {}", gameId, e);
            throw e;
        }
    }

    @GetMapping("/between")
    public ResponseEntity<ApiResponse<List<PlaySession>>> getPlaySessionsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("📝 ENTER getPlaySessionsBetween(start: {}, end: {})", start, end);

        try {
            log.debug("🗂 Querying play sessions between {} and {}", start, end);
            List<PlaySession> sessions = playSessionRepository.findByDatetimeOpenedBetween(start, end);
            log.info("✅ Retrieved {} play sessions between {} and {}", sessions.size(), start, end);

            ApiResponse<List<PlaySession>> response = ApiResponse.success("Play sessions retrieved", sessions);
            log.info("📝 EXIT getPlaySessionsBetween() - count: {}", sessions.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getPlaySessionsBetween() for range {}-{}", start, end, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlaySession>> createPlaySession(@RequestBody PlaySession session) {
        log.info("📝 ENTER createPlaySession(userId: {}, gameId: {})",
                session.getUserId(), session.getGameId());

        try {
            log.debug("🗂 Saving new play session");
            PlaySession saved = playSessionRepository.save(session);
            log.info("✅ Play session created: User {} playing Game {} at {}",
                    saved.getUserId(), saved.getGameId(), saved.getDatetimeOpened());

            ApiResponse<PlaySession> response = ApiResponse.success("Play session created", saved);
            log.info("📝 EXIT createPlaySession() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createPlaySession() for user {} game {}",
                    session.getUserId(), session.getGameId(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePlaySession(@PathVariable String id) {
        log.info("📝 ENTER deletePlaySession(id: {})", id);

        try {
            log.debug("🔍 Checking if play session exists: {}", id);
            if (!playSessionRepository.existsById(id)) {
                log.warn("❌ Play session not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Play session not found");
                log.info("📝 EXIT deletePlaySession() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting play session: {}", id);
            playSessionRepository.deleteById(id);
            log.info("✅ Play session deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Play session deleted", null);
            log.info("📝 EXIT deletePlaySession() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deletePlaySession() for ID: {}", id, e);
            throw e;
        }
    }
}