package com.videogamedb.controllers;

import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.models.Follow;
import com.videogamedb.repositories.FollowRepository;
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
        List<Follow> follows = followRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Follows retrieved", follows));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Follow>> getFollowById(@PathVariable String id) {
        Optional<Follow> follow = followRepository.findById(id);
        return follow
            .map(f -> ResponseEntity.ok(ApiResponse.success("Follow found", f)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Follow not found")));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowers(@PathVariable String userId) {
        List<Follow> follows = followRepository.findByFollowedId(userId);
        return ResponseEntity.ok(ApiResponse.success("Followers retrieved", follows));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<Follow>>> getFollowing(@PathVariable String userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        return ResponseEntity.ok(ApiResponse.success("Following retrieved", follows));
    }

    @GetMapping("/count/followers/{userId}")
    public ResponseEntity<ApiResponse<Long>> getFollowerCount(@PathVariable String userId) {
        long count = followRepository.countByFollowedId(userId);
        return ResponseEntity.ok(ApiResponse.success("Follower count retrieved", count));
    }

    @GetMapping("/count/following/{userId}")
    public ResponseEntity<ApiResponse<Long>> getFollowingCount(@PathVariable String userId) {
        long count = followRepository.countByFollowerId(userId);
        return ResponseEntity.ok(ApiResponse.success("Following count retrieved", count));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Follow>> createFollow(@RequestBody Follow follow) {
        // Check if follow already exists
        if (followRepository.existsByFollowerIdAndFollowedId(
                follow.getFollowerId(), follow.getFollowedId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Follow relationship already exists"));
        }

        Follow saved = followRepository.save(follow);
        log.info("✅ Follow created: {} follows {}", saved.getFollowerId(), saved.getFollowedId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Follow created", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteFollow(@PathVariable String id) {
        if (!followRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Follow not found"));
        }
        followRepository.deleteById(id);
        log.info("✅ Follow deleted: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Follow deleted", null));
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<ApiResponse<Object>> unfollow(
            @RequestParam String followerId,
            @RequestParam String followedId) {
        Optional<Follow> follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId);
        if (follow.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Follow relationship not found"));
        }
        followRepository.delete(follow.get());
        log.info("✅ Unfollowed: {} unfollowed {}", followerId, followedId);
        return ResponseEntity.ok(ApiResponse.success("Unfollowed successfully", null));
    }
}
