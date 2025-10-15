package com.videogamedb.repositories;

import com.videogamedb.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsernameHash(String usernameHash);
    Optional<User> findByEmailEnc(String emailEnc);
    boolean existsByUsernameHash(String usernameHash);
    boolean existsByEmailEnc(String emailEnc);
}
