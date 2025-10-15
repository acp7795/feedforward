package com.videogamedb.repositories;

import com.videogamedb.models.AccessTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessTimeRepository extends MongoRepository<AccessTime, String> {
    List<AccessTime> findByUserId(String userId);
    List<AccessTime> findByTimeAfter(LocalDateTime time);
    List<AccessTime> findByTimeBetween(LocalDateTime start, LocalDateTime end);
    List<AccessTime> findByUserIdOrderByTimeDesc(String userId);
}
