package com.videogamedb.repositories;

import com.videogamedb.models.PlaySession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaySessionRepository extends MongoRepository<PlaySession, String> {
    List<PlaySession> findByUserId(String userId);
    List<PlaySession> findByGameId(String gameId);
    List<PlaySession> findByUserIdAndGameId(String userId, String gameId);
    List<PlaySession> findByDatetimeOpenedBetween(LocalDateTime start, LocalDateTime end);
    List<PlaySession> findByUserIdOrderByDatetimeOpenedDesc(String userId);
}
