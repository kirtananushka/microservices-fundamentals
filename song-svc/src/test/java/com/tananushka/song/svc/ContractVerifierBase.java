package com.tananushka.song.svc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMessageVerifier
@ActiveProfiles("test")
// run with `mvn clean test` command
public class ContractVerifierBase {

    private static final Logger logger = LoggerFactory.getLogger(ContractVerifierBase.class);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        logger.info("MockMvc setup completed.");
    }

    @Test
    void contextLoads() {
        logger.info("Context loads test passed.");
    }
}
