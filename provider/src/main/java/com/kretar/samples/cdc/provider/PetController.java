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
        System.out.println("Looking for a pet");
        Pet pet = petRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        System.out.println("Found myself a pet:" + pet.getId());
        return pet;
    }
}
