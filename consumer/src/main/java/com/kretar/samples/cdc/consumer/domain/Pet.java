package com.kretar.samples.cdc.consumer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Set;

@Value
public class Pet {

    String id;
    String name;
    Category category;
    Set<String> photoUrls;
    Set<Tag> tags;
    String status;

    @JsonCreator
    public Pet(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("category") Category category,
            @JsonProperty("photoUrls") Set<String> photoUrls,
            @JsonProperty("tags") Set<Tag> tags,
            @JsonProperty("status") String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.status = status;
    }
}
