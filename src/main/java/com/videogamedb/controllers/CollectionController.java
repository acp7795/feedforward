package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Collection;
import com.videogamedb.repositories.CollectionRepository;
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
        List<Collection> collections = collectionRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Collections retrieved", collections));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Collection>> getCollectionById(@PathVariable String id) {
        Optional<Collection> collection = collectionRepository.findById(id);
        return collection
            .map(c -> ResponseEntity.ok(ApiResponse.success("Collection found", c)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Collection not found")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Collection>>> getCollectionsByUser(@PathVariable String userId) {
        List<Collection> collections = collectionRepository.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("User collections retrieved", collections));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Collection>>> searchCollections(@RequestParam String name) {
        List<Collection> collections = collectionRepository.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(ApiResponse.success("Collections found", collections));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Collection>> createCollection(@RequestBody Collection collection) {
        Collection saved = collectionRepository.save(collection);
        log.info("✅ Collection created: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Collection created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Collection>> updateCollection(
            @PathVariable String id,
            @RequestBody Collection collection) {
        if (!collectionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Collection not found"));
        }
        collection.setId(id);
        Collection updated = collectionRepository.save(collection);
        log.info("✅ Collection updated: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Collection updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCollection(@PathVariable String id) {
        if (!collectionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Collection not found"));
        }
        collectionRepository.deleteById(id);
        log.info("✅ Collection deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Collection deleted", null));
    }
}
