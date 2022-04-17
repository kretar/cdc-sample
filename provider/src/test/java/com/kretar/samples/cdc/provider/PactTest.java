package com.kretar.samples.cdc.provider;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.Optional;

@Provider("petstore_api")
@PactBroker(host="localhost",port="80")
@Configuration
@Profile("pact-test")
public class PactTest {
    private static ConfigurableApplicationContext context;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeAll
    public static void startService() {
        context = new SpringApplicationBuilder().profiles("pact-test").sources(Application.class, PactTest.class).run();
    }

    @LocalServerPort
    private int port;
    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @AfterAll
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
