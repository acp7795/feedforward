package com.videogamedb.repositories;

import com.videogamedb.models.OwnedGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnedGameRepository extends MongoRepository<OwnedGame, String> {
    List<OwnedGame> findByUserId(String userId);
    List<OwnedGame> findByGameId(String gameId);
    Optional<OwnedGame> findByUserIdAndGameId(String userId, String gameId);
    long countByUserId(String userId);
    long countByGameId(String gameId);
    boolean existsByUserIdAndGameId(String userId, String gameId);
}
