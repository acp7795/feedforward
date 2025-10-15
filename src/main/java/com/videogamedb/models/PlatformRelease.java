package com.videogamedb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "platformReleases")
@CompoundIndex(name = "game_platform_idx", def = "{'game_id': 1, 'platform_id': 1}", unique = true)
public class PlatformRelease {
    
    @Id
    private String id;
    
    @Field("game_id")
    @Indexed
    private String gameId;
    
    @Field("platform_id")
    @Indexed
    private String platformId;
    
    @Indexed
    private Double price;
    
    @Field("releaseDate")
    @Indexed
    private LocalDateTime releaseDate;
}
