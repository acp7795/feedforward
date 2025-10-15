package com.videogamedb.repositories;

import com.videogamedb.models.Genre;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends MongoRepository<Genre, String> {
    Optional<Genre> findByGenreName(String genreName);
    boolean existsByGenreName(String genreName);
}
