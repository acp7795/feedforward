package com.videogamedb.repositories;

import com.videogamedb.models.VideoGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoGameRepository extends MongoRepository<VideoGame, String> {
    List<VideoGame> findByTitleContainingIgnoreCase(String title);
    List<VideoGame> findByEsrb(String esrb);
    List<VideoGame> findByDevelopersContaining(String developerId);
    List<VideoGame> findByPublishersContaining(String publisherId);
    List<VideoGame> findByGenresContaining(String genreId);
}
