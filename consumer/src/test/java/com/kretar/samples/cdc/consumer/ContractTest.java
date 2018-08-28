package com.kretar.samples.cdc.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.google.common.collect.ImmutableMap;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ContractTest {

    private static final String UNKNOWN_PET_ID = "unknown";
    private static final String KNOWN_PET_ID = "known";
    @Rule
    public PactProviderRuleMk2 petstoreApi = new PactProviderRuleMk2("petstore_api", PactSpecVersion.V3, this);

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
                    a.array("tags", tags -> {
                        tags.object(tag -> {
                            tag.stringType("id");
                            tag.stringType("name");
                        });
                    });
                    a.stringType("status");
                }).build())
                .toPact();
    }

    @Test
    @PactVerification(value="petstore_api", fragment="createFragment")
    public void it_should_find_pet_by_id() {
        PetService petService = new PetService(petstoreApi.getUrl());
        Assert.assertFalse(petService.findById(UNKNOWN_PET_ID).isPresent());
        Assert.assertTrue(petService.findById(KNOWN_PET_ID).isPresent());
    }
}