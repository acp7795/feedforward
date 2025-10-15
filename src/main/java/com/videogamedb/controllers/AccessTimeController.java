package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.AccessTime;
import com.videogamedb.repositories.AccessTimeRepository;
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
@RequestMapping("/api/access-times")
@RequiredArgsConstructor
public class AccessTimeController {

    private final AccessTimeRepository accessTimeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAllAccessTimes() {
        log.info("📝 ENTER getAllAccessTimes()");
        try {
            log.debug("🗂 Starting database query for all access times");
            List<AccessTime> accessTimes = accessTimeRepository.findAll();
            log.info("✅ Retrieved {} access times", accessTimes.size());

            ApiResponse<List<AccessTime>> response = ApiResponse.success("Access times retrieved", accessTimes);
            log.info("📝 EXIT getAllAccessTimes() - response: {}", LogHelper.formatCollectionSummary(accessTimes));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllAccessTimes()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccessTime>> getAccessTimeById(@PathVariable String id) {
        log.info("📝 ENTER getAccessTimeById(id: {})", id);
        log.info(LogHelper.formatSensitiveData("AccessTime ID", id));

        try {
            log.debug("🗂 Querying database for access time with ID: {}", id);
            Optional<AccessTime> accessTime = accessTimeRepository.findById(id);

            if (accessTime.isPresent()) {
                AccessTime found = accessTime.get();
                log.info("✅ Found access time: {}", LogHelper.formatObjectSummary(found));
                ApiResponse<AccessTime> response = ApiResponse.success("Access time found", found);
                log.info("📝 EXIT getAccessTimeById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Access time not found for ID: {}", id);
                ApiResponse<AccessTime> response = ApiResponse.error("Access time not found");
                log.info("📝 EXIT getAccessTimeById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getAccessTimeById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAccessTimesByUser(@PathVariable String userId) {
        log.info("📝 ENTER getAccessTimesByUser(userId: {})", userId);
        log.info(LogHelper.formatSensitiveData("User ID", userId));

        try {
            log.debug("🗂 Querying access times for user: {}", userId);
            List<AccessTime> accessTimes = accessTimeRepository.findByUserIdOrderByTimeDesc(userId);
            log.info("✅ Retrieved {} access times for user: {}", accessTimes.size(), userId);

            ApiResponse<List<AccessTime>> response = ApiResponse.success("User access times retrieved", accessTimes);
            log.info("📝 EXIT getAccessTimesByUser() - count: {}", accessTimes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAccessTimesByUser() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/after/{time}")
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAccessTimesAfter(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        log.info("📝 ENTER getAccessTimesAfter(time: {})", time);

        try {
            log.debug("🗂 Querying access times after: {}", time);
            List<AccessTime> accessTimes = accessTimeRepository.findByTimeAfter(time);
            log.info("✅ Retrieved {} access times after: {}", accessTimes.size(), time);

            ApiResponse<List<AccessTime>> response = ApiResponse.success("Access times retrieved", accessTimes);
            log.info("📝 EXIT getAccessTimesAfter() - count: {}", accessTimes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAccessTimesAfter() for time: {}", time, e);
            throw e;
        }
    }

    @GetMapping("/between")
    public ResponseEntity<ApiResponse<List<AccessTime>>> getAccessTimesBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("📝 ENTER getAccessTimesBetween(start: {}, end: {})", start, end);

        try {
            log.debug("🗂 Querying access times between {} and {}", start, end);
            List<AccessTime> accessTimes = accessTimeRepository.findByTimeBetween(start, end);
            log.info("✅ Retrieved {} access times between {} and {}", accessTimes.size(), start, end);

            ApiResponse<List<AccessTime>> response = ApiResponse.success("Access times retrieved", accessTimes);
            log.info("📝 EXIT getAccessTimesBetween() - count: {}", accessTimes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAccessTimesBetween() for range {}-{}", start, end, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccessTime>> createAccessTime(@RequestBody AccessTime accessTime) {
        log.info("📝 ENTER createAccessTime(accessTime: {})", LogHelper.formatObjectSummary(accessTime));
        log.info(LogHelper.formatSensitiveData("AccessTime User ID", accessTime.getUserId()));

        try {
            log.debug("🗂 Saving new access time to database");
            AccessTime saved = accessTimeRepository.save(accessTime);
            log.info("✅ Access time created: {}", saved.getId());

            ApiResponse<AccessTime> response = ApiResponse.success("Access time created", saved);
            log.info("📝 EXIT createAccessTime() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createAccessTime()", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAccessTime(@PathVariable String id) {
        log.info("📝 ENTER deleteAccessTime(id: {})", id);
        log.info(LogHelper.formatSensitiveData("AccessTime ID to delete", id));

        try {
            log.debug("🗂 Checking if access time exists: {}", id);
            if (!accessTimeRepository.existsById(id)) {
                log.warn("❌ Access time not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Access time not found");
                log.info("📝 EXIT deleteAccessTime() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting access time: {}", id);
            accessTimeRepository.deleteById(id);
            log.info("✅ Access time deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Access time deleted", null);
            log.info("📝 EXIT deleteAccessTime() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deleteAccessTime() for ID: {}", id, e);
            throw e;
        }
    }
}