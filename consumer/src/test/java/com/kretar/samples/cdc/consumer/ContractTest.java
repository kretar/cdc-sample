package com.kretar.samples.cdc.consumer;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.common.collect.ImmutableMap;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "petstore_api")
public class ContractTest {

    private static final String UNKNOWN_PET_ID = "unknown";
    private static final String KNOWN_PET_ID = "known";

    @Pact(consumer = "consumer")
    public RequestResponsePact createFragment(PactDslWithProvider builder) {
        return builder
                .given("Known pets", ImmutableMap.of("KNOWN_PET_ID", KNOWN_PET_ID))
                .uponReceiving("A request for an unknown pet")
                .method("GET")
                .path("/pets/" + UNKNOWN_PET_ID)
                .willRespondWith()
                .status(HttpStatus.NOT_FOUND.value())
                .body("{\"error\":\"Not Found\"}")
                .uponReceiving("A request for the first page of pets")
                .method("GET")
                .path("/pets/" + KNOWN_PET_ID)
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body(LambdaDsl.newJsonBody(a -> {
                    a.stringType("id");
                    a.object("category", c -> {
                        c.stringType("id", "1");
                        c.stringType("name", "dog");
                    });
                    a.stringType("name");
                    a.array("photoUrls", urls -> {});
                    a.array("tags", tags ->
                        tags.object(tag -> {
                            tag.stringType("id");
                            tag.stringType("name");
                        }));
                    a.stringType("status");
                }).build())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createFragment")
    public void it_should_find_pet_by_id() {
        PetService petService = new PetService("");
        Assertions.assertFalse(petService.findById(UNKNOWN_PET_ID).isPresent());
        Assertions.assertTrue(petService.findById(KNOWN_PET_ID).isPresent());
    }
}
