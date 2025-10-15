package com.videogamedb.repositories;

import com.videogamedb.models.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByFollowerId(String followerId);
    List<Follow> findByFollowedId(String followedId);
    Optional<Follow> findByFollowerIdAndFollowedId(String followerId, String followedId);
    boolean existsByFollowerIdAndFollowedId(String followerId, String followedId);
    long countByFollowerId(String followerId);
    long countByFollowedId(String followedId);
}
