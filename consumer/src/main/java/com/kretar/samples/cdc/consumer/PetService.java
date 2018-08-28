package com.kretar.samples.cdc.consumer;

import com.kretar.samples.cdc.consumer.domain.Pet;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class PetService {

    final private String apiUrl;

    public PetService(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Optional<Pet> findById(String petId) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            return Optional.of(restTemplate.getForObject(apiUrl + "/pets/" + petId, Pet.class));
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }
}
