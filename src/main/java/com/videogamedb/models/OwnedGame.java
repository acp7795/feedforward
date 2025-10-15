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
@Document(collection = "owned")
@CompoundIndex(name = "user_game_idx", def = "{'user_id': 1, 'game_id': 1}", unique = true)
public class OwnedGame {
    
    @Id
    private String id;
    
    @Field("user_id")
    @Indexed
    private String userId;
    
    @Field("game_id")
    @Indexed
    private String gameId;
    
    @Field("acquisitionDate")
    @Indexed
    private LocalDateTime acquisitionDate;
}
