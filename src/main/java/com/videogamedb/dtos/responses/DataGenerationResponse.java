package com.videogamedb.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataGenerationResponse {
    
    private String status;
    private String message;
    private Map<String, Integer> counts;
    private Object sampleData;
    private boolean savedToDatabase;
}
