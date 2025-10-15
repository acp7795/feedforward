package com.videogamedb.controllers;

import com.videogamedb.dtos.requests.DataGenerationRequest;
import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.dtos.responses.DataGenerationResponse;
import com.videogamedb.services.DataGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Data generation endpoints for seeding mock data.
 */
@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataGeneratorController {

    private final DataGenerationService dataGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateData(
            @RequestBody DataGenerationRequest request) {
        log.info("🎲 Data generation request: {}", request);

        try {
            DataGenerationService.GeneratedData data;

            String datasetType = request.getDatasetType() != null ? request.getDatasetType() : "all";

            switch (datasetType.toLowerCase()) {
                case "all":
                    data = dataGenerationService.generateAllData(
                        request.getNumUsers(),
                        request.getNumGames(),
                        request.getNumContributors(),
                        request.getNumPlatforms(),
                        request.getNumGenres(),
                        request.getEncryptFields(),
                        request.getHashPasswords()
                    );
                    break;
                default:
                    data = dataGenerationService.generateAllData(
                        request.getNumUsers(),
                        request.getNumGames(),
                        request.getNumContributors(),
                        request.getNumPlatforms(),
                        request.getNumGenres(),
                        request.getEncryptFields(),
                        request.getHashPasswords()
                    );
            }

            // Save to database if requested
            if (request.getSaveToDb() != null && request.getSaveToDb()) {
                dataGenerationService.saveDataToDatabase(data);
            }

            // Build response
            Map<String, Integer> counts = new HashMap<>();
            counts.put("platforms", data.getPlatforms().size());
            counts.put("genres", data.getGenres().size());
            counts.put("contributors", data.getContributors().size());
            counts.put("videogames", data.getVideoGames().size());
            counts.put("platformReleases", data.getPlatformReleases().size());
            counts.put("users", data.getUsers().size());
            counts.put("ownedGames", data.getOwnedGames().size());
            counts.put("playSessions", data.getPlaySessions().size());
            counts.put("ratings", data.getRatings().size());
            counts.put("accessTimes", data.getAccessTimes().size());
            counts.put("follows", data.getFollows().size());
            counts.put("collections", data.getCollections().size());

            // Sample data (first user with plain password)
            Map<String, Object> sampleData = new HashMap<>();
            if (!data.getUsers().isEmpty()) {
                sampleData.put("sampleUserId", data.getUsers().get(0).getId());
                if (!data.getPlainUserPasswords().isEmpty()) {
                    String firstUsername = data.getPlainUserPasswords().keySet().iterator().next();
                    sampleData.put("sampleUsername", firstUsername);
                    sampleData.put("samplePassword", data.getPlainUserPasswords().get(firstUsername));
                }
            }

            DataGenerationResponse response = DataGenerationResponse.builder()
                .status("success")
                .message("Data generated successfully")
                .counts(counts)
                .sampleData(sampleData)
                .savedToDatabase(request.getSaveToDb() != null && request.getSaveToDb())
                .build();

            log.info("✅ Data generation complete: {}", counts);

            return ResponseEntity.ok(ApiResponse.success("Data generated successfully", response));
        } catch (Exception e) {
            log.error("❌ Data generation failed", e);
            return ResponseEntity
                .status(500)
                .body(ApiResponse.error("Data generation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/generate/users")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateUsers(
            @RequestParam(defaultValue = "50") int count) {
        DataGenerationRequest request = new DataGenerationRequest();
        request.setDatasetType("all");
        request.setNumUsers(count);
        request.setNumGames(10);
        request.setNumContributors(20);
        request.setNumPlatforms(5);
        request.setNumGenres(10);
        request.setEncryptFields(true);
        request.setHashPasswords(true);
        request.setSaveToDb(true);

        return generateData(request);
    }

    @PostMapping("/generate/games")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateGames(
            @RequestParam(defaultValue = "30") int count) {
        DataGenerationRequest request = new DataGenerationRequest();
        request.setDatasetType("all");
        request.setNumUsers(10);
        request.setNumGames(count);
        request.setNumContributors(40);
        request.setNumPlatforms(5);
        request.setNumGenres(10);
        request.setEncryptFields(true);
        request.setHashPasswords(true);
        request.setSaveToDb(true);

        return generateData(request);
    }

    @PostMapping("/generate/all")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateAll() {
        DataGenerationRequest request = new DataGenerationRequest();
        request.setDatasetType("all");
        request.setNumUsers(50);
        request.setNumGames(30);
        request.setNumContributors(40);
        request.setNumPlatforms(5);
        request.setNumGenres(10);
        request.setEncryptFields(true);
        request.setHashPasswords(true);
        request.setSaveToDb(true);

        return generateData(request);
    }
}
