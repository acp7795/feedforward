package com.videogamedb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "collections")
public class Collection {
    
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    private String description;
    
    @Field("user_id")
    @Indexed
    private String userId;
    
    private List<String> games; // Game IDs in this collection
}
