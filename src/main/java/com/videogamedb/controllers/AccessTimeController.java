package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.AccessTime;
import com.videogamedb.repositories.AccessTimeRepository;
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
@RequestMapping("/api/access-times")
@RequiredArgsConstructor
public class AccessTimeController {

    private final AccessTimeRepository accessTimeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAllAccessTimes() {
        List<AccessTime> accessTimes = accessTimeRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Access times retrieved", accessTimes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccessTime>> getAccessTimeById(@PathVariable String id) {
        Optional<AccessTime> accessTime = accessTimeRepository.findById(id);
        return accessTime
            .map(a -> ResponseEntity.ok(ApiResponse.success("Access time found", a)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Access time not found")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAccessTimesByUser(@PathVariable String userId) {
        List<AccessTime> accessTimes = accessTimeRepository.findByUserIdOrderByTimeDesc(userId);
        return ResponseEntity.ok(ApiResponse.success("User access times retrieved", accessTimes));
    }

    @GetMapping("/after/{time}")
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAccessTimesAfter(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        List<AccessTime> accessTimes = accessTimeRepository.findByTimeAfter(time);
        return ResponseEntity.ok(ApiResponse.success("Access times retrieved", accessTimes));
    }

    @GetMapping("/between")
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAccessTimesBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AccessTime> accessTimes = accessTimeRepository.findByTimeBetween(start, end);
        return ResponseEntity.ok(ApiResponse.success("Access times retrieved", accessTimes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccessTime>> createAccessTime(@RequestBody AccessTime accessTime) {
        AccessTime saved = accessTimeRepository.save(accessTime);
        log.info("✅ Access time created: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Access time created", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAccessTime(@PathVariable String id) {
        if (!accessTimeRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Access time not found"));
        }
        accessTimeRepository.deleteById(id);
        log.info("✅ Access time deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Access time deleted", null));
    }
}
