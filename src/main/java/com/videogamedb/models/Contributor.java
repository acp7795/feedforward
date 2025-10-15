package com.videogamedb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contributors")
public class Contributor {
    
    @Id
    private String id;
    
    @Field("contributor_name")
    @Indexed
    private String contributorName;
    
    @Indexed
    private String type; // "developer" or "publisher"
}
