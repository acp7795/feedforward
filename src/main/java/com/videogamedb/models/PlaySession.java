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
@Document(collection = "plays")
@CompoundIndex(name = "user_game_time_idx", def = "{'user_id': 1, 'game_id': 1, 'datetimeOpened': -1}")
public class PlaySession {
    
    @Id
    private String id;
    
    @Field("user_id")
    @Indexed
    private String userId;
    
    @Field("game_id")
    @Indexed
    private String gameId;
    
    @Field("datetimeOpened")
    @Indexed
    private LocalDateTime datetimeOpened;
    
    @Field("timePlayed")
    private Integer timePlayed; // Time in seconds
}
