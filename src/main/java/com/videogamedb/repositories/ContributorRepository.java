package com.videogamedb.repositories;

import com.videogamedb.models.Contributor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributorRepository extends MongoRepository<Contributor, String> {
    List<Contributor> findByType(String type);
    List<Contributor> findByContributorNameContainingIgnoreCase(String name);
}
