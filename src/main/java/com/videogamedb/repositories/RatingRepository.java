package com.videogamedb.repositories;

import com.videogamedb.models.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {
    List<Rating> findByUserId(String userId);
    List<Rating> findByGameId(String gameId);
    Optional<Rating> findByUserIdAndGameId(String userId, String gameId);
    List<Rating> findByRatingGreaterThanEqual(Integer rating);
}
