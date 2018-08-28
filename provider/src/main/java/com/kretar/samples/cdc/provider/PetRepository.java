package com.kretar.samples.cdc.provider;

import java.util.Optional;

public interface PetRepository {
    Optional<Pet> findById(String petId);
}
