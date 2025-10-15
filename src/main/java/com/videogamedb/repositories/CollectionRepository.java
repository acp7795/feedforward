package com.videogamedb.repositories;

import com.videogamedb.models.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {
    List<Collection> findByUserId(String userId);
    List<Collection> findByNameContainingIgnoreCase(String name);
    List<Collection> findByGamesContaining(String gameId);
}
