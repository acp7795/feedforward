package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Platform;
import com.videogamedb.repositories.PlatformRepository;
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
@RequestMapping("/api/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformRepository platformRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Platform>>> getAllPlatforms() {
        log.info("📝 ENTER getAllPlatforms()");

        try {
            log.debug("🗂 Querying all platforms from database");
            List<Platform> platforms = platformRepository.findAll();
            log.info("✅ Retrieved {} platforms", platforms.size());

            ApiResponse<List<Platform>> response = ApiResponse.success("Platforms retrieved", platforms);
            log.info("📝 EXIT getAllPlatforms() - count: {}", platforms.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllPlatforms()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Platform>> getPlatformById(@PathVariable String id) {
        log.info("📝 ENTER getPlatformById(id: {})", id);

        try {
            log.debug("🗂 Querying platform by ID: {}", id);
            Optional<Platform> platform = platformRepository.findById(id);

            if (platform.isPresent()) {
                Platform found = platform.get();
                log.info("✅ Found platform: {} ({})", found.getPlatformName(), found.getId());
                ApiResponse<Platform> response = ApiResponse.success("Platform found", found);
                log.info("📝 EXIT getPlatformById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Platform not found for ID: {}", id);
                ApiResponse<Platform> response = ApiResponse.error("Platform not found");
                log.info("📝 EXIT getPlatformById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getPlatformById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Platform>> getPlatformByName(@PathVariable String name) {
        log.info("📝 ENTER getPlatformByName(name: {})", name);

        try {
            log.debug("🗂 Querying platform by name: {}", name);
            Optional<Platform> platform = platformRepository.findByPlatformName(name);

            if (platform.isPresent()) {
                Platform found = platform.get();
                log.info("✅ Found platform by name: {} ({})", name, found.getId());
                ApiResponse<Platform> response = ApiResponse.success("Platform found", found);
                log.info("📝 EXIT getPlatformByName() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Platform not found for name: {}", name);
                ApiResponse<Platform> response = ApiResponse.error("Platform not found");
                log.info("📝 EXIT getPlatformByName() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getPlatformByName() for name: {}", name, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Platform>> createPlatform(@RequestBody Platform platform) {
        log.info("📝 ENTER createPlatform(platform: {})", platform.getPlatformName());

        try {
            log.debug("🔍 Checking if platform already exists: {}", platform.getPlatformName());
            if (platformRepository.existsByPlatformName(platform.getPlatformName())) {
                log.warn("❌ Platform already exists: {}", platform.getPlatformName());
                ApiResponse<Platform> response = ApiResponse.error("Platform already exists");
                log.info("📝 EXIT createPlatform() - conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            log.debug("🗂 Saving new platform");
            Platform saved = platformRepository.save(platform);
            log.info("✅ Platform created: {} ({})", saved.getPlatformName(), saved.getId());

            ApiResponse<Platform> response = ApiResponse.success("Platform created", saved);
            log.info("📝 EXIT createPlatform() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createPlatform() for platform: {}", platform.getPlatformName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Platform>> updatePlatform(
            @PathVariable String id,
            @RequestBody Platform platform) {
        log.info("📝 ENTER updatePlatform(id: {}, platform: {})", id, platform.getPlatformName());

        try {
            log.debug("🔍 Checking if platform exists: {}", id);
            if (!platformRepository.existsById(id)) {
                log.warn("❌ Platform not found for update: {}", id);
                ApiResponse<Platform> response = ApiResponse.error("Platform not found");
                log.info("📝 EXIT updatePlatform() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Updating platform with ID: {}", id);
            platform.setId(id);
            Platform updated = platformRepository.save(platform);
            log.info("✅ Platform updated: {} ({})", updated.getPlatformName(), id);

            ApiResponse<Platform> response = ApiResponse.success("Platform updated", updated);
            log.info("📝 EXIT updatePlatform() - updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in updatePlatform() for ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePlatform(@PathVariable String id) {
        log.info("📝 ENTER deletePlatform(id: {})", id);

        try {
            log.debug("🔍 Checking if platform exists: {}", id);
            if (!platformRepository.existsById(id)) {
                log.warn("❌ Platform not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Platform not found");
                log.info("📝 EXIT deletePlatform() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting platform: {}", id);
            platformRepository.deleteById(id);
            log.info("✅ Platform deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Platform deleted", null);
            log.info("📝 EXIT deletePlatform() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deletePlatform() for ID: {}", id, e);
            throw e;
        }
    }
}