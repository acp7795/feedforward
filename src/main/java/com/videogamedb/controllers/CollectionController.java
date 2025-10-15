package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Collection;
import com.videogamedb.repositories.CollectionRepository;
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
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionRepository collectionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Collection>>> getAllCollections() {
        log.info("📝 ENTER getAllCollections()");

        try {
            log.debug("🗂 Querying all collections from database");
            List<Collection> collections = collectionRepository.findAll();
            log.info("✅ Retrieved {} collections", collections.size());

            ApiResponse<List<Collection>> response = ApiResponse.success("Collections retrieved", collections);
            log.info("📝 EXIT getAllCollections() - count: {}", collections.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllCollections()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Collection>> getCollectionById(@PathVariable String id) {
        log.info("📝 ENTER getCollectionById(id: {})", id);

        try {
            log.debug("🗂 Querying collection by ID: {}", id);
            Optional<Collection> collection = collectionRepository.findById(id);

            if (collection.isPresent()) {
                Collection found = collection.get();
                log.info("✅ Found collection: {}", LogHelper.formatObjectSummary(found));
                ApiResponse<Collection> response = ApiResponse.success("Collection found", found);
                log.info("📝 EXIT getCollectionById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Collection not found for ID: {}", id);
                ApiResponse<Collection> response = ApiResponse.error("Collection not found");
                log.info("📝 EXIT getCollectionById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getCollectionById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Collection>>> getCollectionsByUser(@PathVariable String userId) {
        log.info("📝 ENTER getCollectionsByUser(userId: {})", userId);

        try {
            log.debug("🗂 Querying collections for user: {}", userId);
            List<Collection> collections = collectionRepository.findByUserId(userId);
            log.info("✅ Retrieved {} collections for user: {}", collections.size(), userId);

            ApiResponse<List<Collection>> response = ApiResponse.success("User collections retrieved", collections);
            log.info("📝 EXIT getCollectionsByUser() - count: {}", collections.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getCollectionsByUser() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Collection>>> searchCollections(@RequestParam String name) {
        log.info("📝 ENTER searchCollections(name: {})", name);

        try {
            log.debug("🗂 Searching collections with name containing: {}", name);
            List<Collection> collections = collectionRepository.findByNameContainingIgnoreCase(name);
            log.info("✅ Found {} collections matching: {}", collections.size(), name);

            ApiResponse<List<Collection>> response = ApiResponse.success("Collections found", collections);
            log.info("📝 EXIT searchCollections() - matches: {}", collections.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in searchCollections() for name: {}", name, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Collection>> createCollection(@RequestBody Collection collection) {
        log.info("📝 ENTER createCollection(collection: {})", LogHelper.formatObjectSummary(collection));

        try {
            log.debug("🗂 Saving new collection to database");
            Collection saved = collectionRepository.save(collection);
            log.info("✅ Collection created: {}", saved.getId());

            ApiResponse<Collection> response = ApiResponse.success("Collection created", saved);
            log.info("📝 EXIT createCollection() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createCollection()", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Collection>> updateCollection(
            @PathVariable String id,
            @RequestBody Collection collection) {
        log.info("📝 ENTER updateCollection(id: {}, collection: {})", id, LogHelper.formatObjectSummary(collection));

        try {
            log.debug("🗂 Checking if collection exists: {}", id);
            if (!collectionRepository.existsById(id)) {
                log.warn("❌ Collection not found for update: {}", id);
                ApiResponse<Collection> response = ApiResponse.error("Collection not found");
                log.info("📝 EXIT updateCollection() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Updating collection with ID: {}", id);
            collection.setId(id);
            Collection updated = collectionRepository.save(collection);
            log.info("✅ Collection updated: {}", id);

            ApiResponse<Collection> response = ApiResponse.success("Collection updated", updated);
            log.info("📝 EXIT updateCollection() - updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in updateCollection() for ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCollection(@PathVariable String id) {
        log.info("📝 ENTER deleteCollection(id: {})", id);

        try {
            log.debug("🗂 Checking if collection exists: {}", id);
            if (!collectionRepository.existsById(id)) {
                log.warn("❌ Collection not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Collection not found");
                log.info("📝 EXIT deleteCollection() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting collection: {}", id);
            collectionRepository.deleteById(id);
            log.info("✅ Collection deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Collection deleted", null);
            log.info("📝 EXIT deleteCollection() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deleteCollection() for ID: {}", id, e);
            throw e;
        }
    }
}