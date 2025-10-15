package com.videogamedb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "videogames")
public class VideoGame {
    
    @Id
    private String id;
    
    @Indexed
    private String title;
    
    private String esrb;
    
    @Indexed
    private List<String> developers; // References to Contributor IDs
    
    @Indexed
    private List<String> publishers; // References to Contributor IDs
    
    @Indexed
    private List<String> genres; // References to Genre IDs
}
