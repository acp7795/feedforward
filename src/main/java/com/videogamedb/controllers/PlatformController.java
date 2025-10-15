package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Platform;
import com.videogamedb.repositories.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformRepository platformRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Platform>>> getAllPlatforms() {
        List<Platform> platforms = platformRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Platforms retrieved", platforms));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Platform>> getPlatformById(@PathVariable String id) {
        Optional<Platform> platform = platformRepository.findById(id);
        return platform
            .map(p -> ResponseEntity.ok(ApiResponse.success("Platform found", p)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Platform not found")));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Platform>> getPlatformByName(@PathVariable String name) {
        Optional<Platform> platform = platformRepository.findByPlatformName(name);
        return platform
            .map(p -> ResponseEntity.ok(ApiResponse.success("Platform found", p)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Platform not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Platform>> createPlatform(@RequestBody Platform platform) {
        if (platformRepository.existsByPlatformName(platform.getPlatformName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Platform already exists"));
        }
        Platform saved = platformRepository.save(platform);
        log.info("✅ Platform created: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Platform created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Platform>> updatePlatform(
            @PathVariable String id,
            @RequestBody Platform platform) {
        if (!platformRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Platform not found"));
        }
        platform.setId(id);
        Platform updated = platformRepository.save(platform);
        log.info("✅ Platform updated: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Platform updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePlatform(@PathVariable String id) {
        if (!platformRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Platform not found"));
        }
        platformRepository.deleteById(id);
        log.info("✅ Platform deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Platform deleted", null));
    }
}
