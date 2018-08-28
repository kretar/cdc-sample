package com.kretar.samples.cdc.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PetController {

    private final PetRepository petRepository;

    @Autowired
    public PetController(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @GetMapping("/pets/{id}")
    public Pet findById(@PathVariable String id) {
        return petRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
