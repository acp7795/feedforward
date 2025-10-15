package com.videogamedb.repositories;

import com.videogamedb.models.PlatformRelease;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformReleaseRepository extends MongoRepository<PlatformRelease, String> {
    List<PlatformRelease> findByGameId(String gameId);
    List<PlatformRelease> findByPlatformId(String platformId);
    Optional<PlatformRelease> findByGameIdAndPlatformId(String gameId, String platformId);
    List<PlatformRelease> findByPriceBetween(Double minPrice, Double maxPrice);
    List<PlatformRelease> findByReleaseDateBetween(LocalDateTime start, LocalDateTime end);
}
