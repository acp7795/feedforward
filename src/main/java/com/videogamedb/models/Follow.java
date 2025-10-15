package com.videogamedb.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follows")
@CompoundIndex(name = "follower_followed_idx", def = "{'follower_id': 1, 'followed_id': 1}", unique = true)
public class Follow {
    
    @Id
    private String id;
    
    @Field("follower_id")
    @Indexed
    private String followerId;
    
    @Field("followed_id")
    @Indexed
    private String followedId;
}
