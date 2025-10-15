package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.VideoGame;
import com.videogamedb.repositories.VideoGameRepository;
import com.videogamedb.utils.LogHelper;
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
        log.info("📝 ENTER getAllGames(page: {}, size: {}, sortBy: {})", page, size, sortBy);

        try {
            log.debug("📄 Creating pageable request - page: {}, size: {}, sort: {}", page, size, sortBy);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

            log.debug("🗂 Querying paginated games from database");
            Page<VideoGame> games = videoGameRepository.findAll(pageable);
            log.info("✅ Retrieved {} games (page {} of {}, total: {})",
                    games.getNumberOfElements(), page, games.getTotalPages(), games.getTotalElements());

            ApiResponse<Page<VideoGame>> response = ApiResponse.success("Games retrieved successfully", games);
            log.info("📝 EXIT getAllGames() - page: {}/{}, size: {}",
                    page, games.getTotalPages(), games.getNumberOfElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllGames() for page {} size {}", page, size, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoGame>> getGameById(@PathVariable String id) {
        log.info("📝 ENTER getGameById(id: {})", id);

        try {
            log.debug("🗂 Querying game by ID: {}", id);
            Optional<VideoGame> game = videoGameRepository.findById(id);

            if (game.isPresent()) {
                VideoGame found = game.get();
                log.info("✅ Found game: {} ({})", found.getTitle(), found.getId());
                ApiResponse<VideoGame> response = ApiResponse.success("Game found", found);
                log.info("📝 EXIT getGameById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Game not found for ID: {}", id);
                ApiResponse<VideoGame> response = ApiResponse.error("Game not found");
                log.info("📝 EXIT getGameById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getGameById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VideoGame>>> searchGames(@RequestParam String title) {
        log.info("📝 ENTER searchGames(title: {})", title);

        try {
            log.debug("🔍 Searching games with title containing: {}", title);
            List<VideoGame> games = videoGameRepository.findByTitleContainingIgnoreCase(title);
            log.info("✅ Found {} games matching: {}", games.size(), title);

            ApiResponse<List<VideoGame>> response = ApiResponse.success("Games found", games);
            log.info("📝 EXIT searchGames() - matches: {}", games.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in searchGames() for title: {}", title, e);
            throw e;
        }
    }

    @GetMapping("/esrb/{rating}")
    public ResponseEntity<ApiResponse<List<VideoGame>>> getGamesByEsrb(@PathVariable String rating) {
        log.info("📝 ENTER getGamesByEsrb(rating: {})", rating);

        try {
            log.debug("🗂 Querying games by ESRB rating: {}", rating);
            List<VideoGame> games = videoGameRepository.findByEsrb(rating);
            log.info("✅ Found {} games with ESRB rating: {}", games.size(), rating);

            ApiResponse<List<VideoGame>> response = ApiResponse.success("Games found", games);
            log.info("📝 EXIT getGamesByEsrb() - count: {}", games.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getGamesByEsrb() for rating: {}", rating, e);
            throw e;
        }
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<VideoGame>>> getGamesByGenre(@PathVariable String genreId) {
        log.info("📝 ENTER getGamesByGenre(genreId: {})", genreId);

        try {
            log.debug("🗂 Querying games by genre: {}", genreId);
            List<VideoGame> games = videoGameRepository.findByGenresContaining(genreId);
            log.info("✅ Found {} games in genre: {}", games.size(), genreId);

            ApiResponse<List<VideoGame>> response = ApiResponse.success("Games found", games);
            log.info("📝 EXIT getGamesByGenre() - count: {}", games.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getGamesByGenre() for genre: {}", genreId, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VideoGame>> createGame(@RequestBody VideoGame game) {
        log.info("📝 ENTER createGame(game: {})", game.getTitle());

        try {
            log.debug("🗂 Saving new game to database");
            VideoGame saved = videoGameRepository.save(game);
            log.info("✅ Game created: {} ({})", saved.getTitle(), saved.getId());

            ApiResponse<VideoGame> response = ApiResponse.success("Game created", saved);
            log.info("📝 EXIT createGame() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createGame() for title: {}", game.getTitle(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoGame>> updateGame(
            @PathVariable String id,
            @RequestBody VideoGame game) {
        log.info("📝 ENTER updateGame(id: {}, game: {})", id, game.getTitle());

        try {
            log.debug("🔍 Checking if game exists: {}", id);
            if (!videoGameRepository.existsById(id)) {
                log.warn("❌ Game not found for update: {}", id);
                ApiResponse<VideoGame> response = ApiResponse.error("Game not found");
                log.info("📝 EXIT updateGame() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Updating game with ID: {}", id);
            game.setId(id);
            VideoGame updated = videoGameRepository.save(game);
            log.info("✅ Game updated: {} ({})", updated.getTitle(), id);

            ApiResponse<VideoGame> response = ApiResponse.success("Game updated", updated);
            log.info("📝 EXIT updateGame() - updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in updateGame() for ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteGame(@PathVariable String id) {
        log.info("📝 ENTER deleteGame(id: {})", id);

        try {
            log.debug("🔍 Checking if game exists: {}", id);
            if (!videoGameRepository.existsById(id)) {
                log.warn("❌ Game not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Game not found");
                log.info("📝 EXIT deleteGame() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting game: {}", id);
            videoGameRepository.deleteById(id);
            log.info("✅ Game deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Game deleted", null);
            log.info("📝 EXIT deleteGame() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deleteGame() for ID: {}", id, e);
            throw e;
        }
    }
}