package com.tananushka.resource.proc.contract.test;

import com.tananushka.resource.proc.ResourceProcApp;
import com.tananushka.resource.proc.client.SongSvcClient;
import com.tananushka.resource.proc.dto.MetadataRequest;
import com.tananushka.resource.proc.dto.SongIdResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ResourceProcApp.class, SongSvcClientContractTestConfig.class})
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("contract-test")
public class SongSvcClientContractTest {

    @Autowired
    @Qualifier("testSongSvcClient")
    private SongSvcClient songSvcClient;

    @BeforeEach
    void setup() {
        stubFor(post(urlEqualTo("/songs"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"resourceId\":1}")
                        .withStatus(200)));
    }

    @Test
    void shouldSaveMetadata() {
        MetadataRequest request = new MetadataRequest();
        request.setId(1);
        request.setArtist("Test Artist");
        request.setName("Test Song");
        request.setAlbum("Test Album");
        request.setYear("2022");
        request.setDuration("03:45");

        SongIdResponse response = songSvcClient.saveMetadata(request);
        assertEquals(1, response.getResourceId());
    }
}
