package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.VideoGame;
import com.videogamedb.repositories.VideoGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class VideoGameController {

    private final VideoGameRepository videoGameRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VideoGame>>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<VideoGame> games = videoGameRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Games retrieved successfully", games));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoGame>> getGameById(@PathVariable String id) {
        Optional<VideoGame> game = videoGameRepository.findById(id);
        return game
            .map(g -> ResponseEntity.ok(ApiResponse.success("Game found", g)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Game not found")));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VideoGame>>> searchGames(@RequestParam String title) {
        List<VideoGame> games = videoGameRepository.findByTitleContainingIgnoreCase(title);
        return ResponseEntity.ok(ApiResponse.success("Games found", games));
    }

    @GetMapping("/esrb/{rating}")
    public ResponseEntity<ApiResponse<List<VideoGame>>> getGamesByEsrb(@PathVariable String rating) {
        List<VideoGame> games = videoGameRepository.findByEsrb(rating);
        return ResponseEntity.ok(ApiResponse.success("Games found", games));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<VideoGame>>> getGamesByGenre(@PathVariable String genreId) {
        List<VideoGame> games = videoGameRepository.findByGenresContaining(genreId);
        return ResponseEntity.ok(ApiResponse.success("Games found", games));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VideoGame>> createGame(@RequestBody VideoGame game) {
        VideoGame saved = videoGameRepository.save(game);
        log.info("✅ Game created: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Game created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoGame>> updateGame(
            @PathVariable String id,
            @RequestBody VideoGame game) {
        if (!videoGameRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Game not found"));
        }
        game.setId(id);
        VideoGame updated = videoGameRepository.save(game);
        log.info("✅ Game updated: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Game updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteGame(@PathVariable String id) {
        if (!videoGameRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Game not found"));
        }
        videoGameRepository.deleteById(id);
        log.info("✅ Game deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Game deleted", null));
    }
}
