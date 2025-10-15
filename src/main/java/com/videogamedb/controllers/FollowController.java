package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Follow;
import com.videogamedb.repositories.FollowRepository;
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
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowRepository followRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Follow>>> getAllFollows() {
        log.info("📝 ENTER getAllFollows()");

        try {
            log.debug("🗂 Querying all follows from database");
            List<Follow> follows = followRepository.findAll();
            log.info("✅ Retrieved {} follows", follows.size());

            ApiResponse<List<Follow>> response = ApiResponse.success("Follows retrieved", follows);
            log.info("📝 EXIT getAllFollows() - count: {}", follows.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getAllFollows()", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Follow>> getFollowById(@PathVariable String id) {
        log.info("📝 ENTER getFollowById(id: {})", id);

        try {
            log.debug("🗂 Querying follow by ID: {}", id);
            Optional<Follow> follow = followRepository.findById(id);

            if (follow.isPresent()) {
                Follow found = follow.get();
                log.info("✅ Found follow: {} -> {}", found.getFollowerId(), found.getFollowedId());
                ApiResponse<Follow> response = ApiResponse.success("Follow found", found);
                log.info("📝 EXIT getFollowById() - found: true");
                return ResponseEntity.ok(response);
            } else {
                log.warn("❌ Follow not found for ID: {}", id);
                ApiResponse<Follow> response = ApiResponse.error("Follow not found");
                log.info("📝 EXIT getFollowById() - found: false");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            log.error("❌ ERROR in getFollowById() for ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowers(@PathVariable String userId) {
        log.info("📝 ENTER getFollowers(userId: {})", userId);

        try {
            log.debug("🗂 Querying followers for user: {}", userId);
            List<Follow> follows = followRepository.findByFollowedId(userId);
            log.info("✅ Retrieved {} followers for user: {}", follows.size(), userId);

            ApiResponse<List<Follow>> response = ApiResponse.success("Followers retrieved", follows);
            log.info("📝 EXIT getFollowers() - count: {}", follows.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getFollowers() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowing(@PathVariable String userId) {
        log.info("📝 ENTER getFollowing(userId: {})", userId);

        try {
            log.debug("🗂 Querying users followed by: {}", userId);
            List<Follow> follows = followRepository.findByFollowerId(userId);
            log.info("✅ User {} is following {} users", userId, follows.size());

            ApiResponse<List<Follow>> response = ApiResponse.success("Following retrieved", follows);
            log.info("📝 EXIT getFollowing() - count: {}", follows.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getFollowing() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/count/followers/{userId}")
    public ResponseEntity<ApiResponse<Long>> getFollowerCount(@PathVariable String userId) {
        log.info("📝 ENTER getFollowerCount(userId: {})", userId);

        try {
            log.debug("🗂 Counting followers for user: {}", userId);
            long count = followRepository.countByFollowedId(userId);
            log.info("✅ User {} has {} followers", userId, count);

            ApiResponse<Long> response = ApiResponse.success("Follower count retrieved", count);
            log.info("📝 EXIT getFollowerCount() - count: {}", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getFollowerCount() for user: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/count/following/{userId}")
    public ResponseEntity<ApiResponse<Long>> getFollowingCount(@PathVariable String userId) {
        log.info("📝 ENTER getFollowingCount(userId: {})", userId);

        try {
            log.debug("🗂 Counting users followed by: {}", userId);
            long count = followRepository.countByFollowerId(userId);
            log.info("✅ User {} is following {} users", userId, count);

            ApiResponse<Long> response = ApiResponse.success("Following count retrieved", count);
            log.info("📝 EXIT getFollowingCount() - count: {}", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in getFollowingCount() for user: {}", userId, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Follow>> createFollow(@RequestBody Follow follow) {
        log.info("📝 ENTER createFollow(follow: {} -> {})", follow.getFollowerId(), follow.getFollowedId());

        try {
            log.debug("🔍 Checking if follow relationship already exists");
            if (followRepository.existsByFollowerIdAndFollowedId(
                    follow.getFollowerId(), follow.getFollowedId())) {
                log.warn("❌ Follow relationship already exists: {} -> {}",
                        follow.getFollowerId(), follow.getFollowedId());
                ApiResponse<Follow> response = ApiResponse.error("Follow relationship already exists");
                log.info("📝 EXIT createFollow() - conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            log.debug("🗂 Saving new follow relationship");
            Follow saved = followRepository.save(follow);
            log.info("✅ Follow created: {} follows {}", saved.getFollowerId(), saved.getFollowedId());

            ApiResponse<Follow> response = ApiResponse.success("Follow created", saved);
            log.info("📝 EXIT createFollow() - created ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ ERROR in createFollow() for {} -> {}",
                    follow.getFollowerId(), follow.getFollowedId(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteFollow(@PathVariable String id) {
        log.info("📝 ENTER deleteFollow(id: {})", id);

        try {
            log.debug("🔍 Checking if follow exists: {}", id);
            if (!followRepository.existsById(id)) {
                log.warn("❌ Follow not found for deletion: {}", id);
                ApiResponse<Object> response = ApiResponse.error("Follow not found");
                log.info("📝 EXIT deleteFollow() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting follow: {}", id);
            followRepository.deleteById(id);
            log.info("✅ Follow deleted: {}", id);

            ApiResponse<Object> response = ApiResponse.success("Follow deleted", null);
            log.info("📝 EXIT deleteFollow() - deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in deleteFollow() for ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<ApiResponse<Object>> unfollow(
            @RequestParam String followerId,
            @RequestParam String followedId) {
        log.info("📝 ENTER unfollow(followerId: {}, followedId: {})", followerId, followedId);

        try {
            log.debug("🔍 Looking up follow relationship");
            Optional<Follow> follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId);
            if (follow.isEmpty()) {
                log.warn("❌ Follow relationship not found: {} -> {}", followerId, followedId);
                ApiResponse<Object> response = ApiResponse.error("Follow relationship not found");
                log.info("📝 EXIT unfollow() - not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            log.debug("🗂 Deleting follow relationship");
            followRepository.delete(follow.get());
            log.info("✅ Unfollowed: {} unfollowed {}", followerId, followedId);

            ApiResponse<Object> response = ApiResponse.success("Unfollowed successfully", null);
            log.info("📝 EXIT unfollow() - unfollowed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ ERROR in unfollow() for {} -> {}", followerId, followedId, e);
            throw e;
        }
    }
}