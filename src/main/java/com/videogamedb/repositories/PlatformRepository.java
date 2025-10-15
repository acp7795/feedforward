package com.videogamedb.repositories;

import com.videogamedb.models.Platform;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends MongoRepository<Platform, String> {
    Optional<Platform> findByPlatformName(String platformName);
    boolean existsByPlatformName(String platformName);
}
