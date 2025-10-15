package com.videogamedb.controllers;

import com.videogamedb.dtos.requests.DataGenerationRequest;
import com.videogamedb.dtos.responses.ApiResponse;
import com.videogamedb.dtos.responses.DataGenerationResponse;
import com.videogamedb.services.DataGenerationService;
import com.videogamedb.utils.LogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataGeneratorController {

    private final DataGenerationService dataGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateData(
            @RequestBody DataGenerationRequest request) {
        log.info("📝 ENTER generateData(request: {})", request);
        log.debug("🎲 Data generation request details: {}", request);

        try {
            log.info("🎲 Data generation request: {}", request);
            log.debug("📝 Starting data generation process with datasetType: {}",
                    request.getDatasetType() != null ? request.getDatasetType() : "all");

            DataGenerationService.GeneratedData data;
            String datasetType = request.getDatasetType() != null ? request.getDatasetType() : "all";

            log.debug("🔄 Processing dataset type: {}", datasetType);
            switch (datasetType.toLowerCase()) {
                case "all":
                    log.info("📊 Generating all data types");
                    data = dataGenerationService.generateAllData(
                            request.getNumUsers(),
                            request.getNumGames(),
                            request.getNumContributors(),
                            request.getNumPlatforms(),
                            request.getNumGenres(),
                            request.getEncryptFields(),
                            request.getHashPasswords());
                    break;
                default:
                    log.warn("❓ Unknown dataset type: {}, defaulting to 'all'", datasetType);
                    data = dataGenerationService.generateAllData(
                            request.getNumUsers(),
                            request.getNumGames(),
                            request.getNumContributors(),
                            request.getNumPlatforms(),
                            request.getNumGenres(),
                            request.getEncryptFields(),
                            request.getHashPasswords());
            }
            log.info("✅ Data generation completed successfully");

            // Save to database if requested
            if (request.getSaveToDb() != null && request.getSaveToDb()) {
                log.debug("💾 Saving generated data to database");
                dataGenerationService.saveDataToDatabase(data);
                log.info("✅ Data saved to database");
            } else {
                log.debug("💾 Skipping database save as requested");
            }

            // Build response
            log.debug("📦 Building response with counts");
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
            log.debug("📋 Collecting sample data for response");
            Map<String, Object> sampleData = new HashMap<>();
            if (!data.getUsers().isEmpty()) {
                sampleData.put("sampleUserId", data.getUsers().get(0).getId());
                if (!data.getPlainUserPasswords().isEmpty()) {
                    String firstUsername = data.getPlainUserPasswords().keySet().iterator().next();
                    sampleData.put("sampleUsername", firstUsername);
                    sampleData.put("samplePassword", data.getPlainUserPasswords().get(firstUsername));
                    log.info(LogHelper.formatSensitiveData("Sample User Credentials",
                            String.format("username: %s, password: %s", firstUsername,
                                    data.getPlainUserPasswords().get(firstUsername))));
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
            log.info("📝 EXIT generateData() - success, counts: {}", counts);
            return ResponseEntity.ok(ApiResponse.success("Data generated successfully", response));
        } catch (Exception e) {
            log.error("❌ Data generation failed", e);
            log.info("📝 EXIT generateData() - failed with error: {}", e.getMessage());
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("Data generation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/generate/users")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateUsers(
            @RequestParam(defaultValue = "50") int count) {
        log.info("📝 ENTER generateUsers(count: {})", count);

        try {
            log.info("👥 Generating {} users with default settings", count);
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

            log.debug("🔄 Delegating to generateData with constructed request");
            log.info("📝 EXIT generateUsers() - delegating to main endpoint");
            return generateData(request);
        } catch (Exception e) {
            log.error("❌ ERROR in generateUsers() for count: {}", count, e);
            throw e;
        }
    }

    @PostMapping("/generate/games")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateGames(
            @RequestParam(defaultValue = "30") int count) {
        log.info("📝 ENTER generateGames(count: {})", count);

        try {
            log.info("🎮 Generating {} games with default settings", count);
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

            log.debug("🔄 Delegating to generateData with constructed request");
            log.info("📝 EXIT generateGames() - delegating to main endpoint");
            return generateData(request);
        } catch (Exception e) {
            log.error("❌ ERROR in generateGames() for count: {}", count, e);
            throw e;
        }
    }

    @PostMapping("/generate/all")
    public ResponseEntity<ApiResponse<DataGenerationResponse>> generateAll() {
        log.info("📝 ENTER generateAll()");

        try {
            log.info("🏗️ Generating complete dataset with default settings");
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

            log.debug("🔄 Delegating to generateData with default request");
            log.info("📝 EXIT generateAll() - delegating to main endpoint");
            return generateData(request);
        } catch (Exception e) {
            log.error("❌ ERROR in generateAll()", e);
            throw e;
        }
    }
}