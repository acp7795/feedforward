package com.videogamedb.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataGenerationRequest {
    
    private String datasetType; // "users", "games", "all"
    private Integer count;
    private Boolean encryptFields = true;
    private Boolean hashPasswords = true;
    private Boolean saveToDb = true;
    
    // Specific counts for "all" type
    private Integer numUsers = 50;
    private Integer numGames = 30;
    private Integer numContributors = 40;
    private Integer numPlatforms = 5;
    private Integer numGenres = 10;
}
