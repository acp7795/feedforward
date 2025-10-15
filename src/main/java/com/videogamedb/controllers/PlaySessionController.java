package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.PlaySession;
import com.videogamedb.repositories.PlaySessionRepository;
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
        List<PlaySession> sessions = playSessionRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Play sessions retrieved", sessions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlaySession>> getPlaySessionById(@PathVariable String id) {
        Optional<PlaySession> session = playSessionRepository.findById(id);
        return session
            .map(s -> ResponseEntity.ok(ApiResponse.success("Play session found", s)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Play session not found")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PlaySession>>> getPlaySessionsByUser(@PathVariable String userId) {
        List<PlaySession> sessions = playSessionRepository.findByUserIdOrderByDatetimeOpenedDesc(userId);
        return ResponseEntity.ok(ApiResponse.success("User play sessions retrieved", sessions));
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<List<PlaySession>>> getPlaySessionsByGame(@PathVariable String gameId) {
        List<PlaySession> sessions = playSessionRepository.findByGameId(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game play sessions retrieved", sessions));
    }

    @GetMapping("/between")
    public ResponseEntity<ApiResponse<List<PlaySession>>> getPlaySessionsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<PlaySession> sessions = playSessionRepository.findByDatetimeOpenedBetween(start, end);
        return ResponseEntity.ok(ApiResponse.success("Play sessions retrieved", sessions));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlaySession>> createPlaySession(@RequestBody PlaySession session) {
        PlaySession saved = playSessionRepository.save(session);
        log.info("✅ Play session created: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Play session created", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePlaySession(@PathVariable String id) {
        if (!playSessionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Play session not found"));
        }
        playSessionRepository.deleteById(id);
        log.info("✅ Play session deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Play session deleted", null));
    }
}
