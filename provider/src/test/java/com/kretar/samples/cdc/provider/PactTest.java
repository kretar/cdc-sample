package com.kretar.samples.cdc.provider;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.Optional;

@RunWith(PactRunner.class)
@Provider("petstore_api")
@PactBroker(host="localhost",port="80")
@Configuration
@Profile("pact-test")
public class PactTest {
    private static ConfigurableApplicationContext context;

    @BeforeClass
    public static void startService() {
        context = new SpringApplicationBuilder().profiles("pact-test").sources(Application.class, PactTest.class).run();
    }

    @TestTarget
    public final Target target = new HttpTarget(8084);

    @AfterClass
    public static void kill() {
        context.stop();
    }

    @Bean
    public PetRepository petRepository() {
        return Mockito.mock(PetRepository.class);
    }

    @State("Known pets")
    public void knownPets(Map<String, Object> data) {
        PetRepository petRepository = context.getBean("petRepository", PetRepository.class);
        String knownPetId = data.get("KNOWN_PET_ID").toString();

        Optional<Pet> optionalPet = buildOptionalPet(knownPetId);

        Mockito.when(petRepository.findById(knownPetId)).thenReturn(optionalPet);
    }

    private Optional<Pet> buildOptionalPet(String petId) {
        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Sharky");
        pet.setStatus(Status.SOLD);
        pet.setCategory(new Category("1", "dog"));
        pet.getTags().add(new Tag("1", "furry"));
        return Optional.of(pet);
    }
}