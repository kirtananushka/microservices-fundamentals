package com.tananushka.resource.svc.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tananushka.resource.svc.ResourceSvcApp;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = {ResourceSvcApp.class, CucumberE2ETestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@CucumberContextConfiguration
@ActiveProfiles("e2e-test")
public class CucumberE2EStepDefs {

    private final String baseUrl = "http://localhost:8071/resources";
    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;
    private byte[] audioData;
    private Integer existingResourceId;

    @Given("a user has an audio file")
    public void a_user_has_an_audio_file() throws IOException {
        audioData = Files.readAllBytes(Path.of("src/test/resources/audio/valid-audio-data.mp3"));
    }

    @When("the user uploads the audio file to the resource-svc")
    public void the_user_uploads_the_audio_file_to_the_resource_svc() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(audioData, headers);
        response = restTemplate.postForEntity(baseUrl, requestEntity, String.class);
    }

    @Then("the resource is successfully uploaded")
    public void the_resource_is_successfully_uploaded() {
        assertEquals(200, response.getStatusCode().value());
    }

    @Then("the user receives the resource details")
    public void the_user_receives_the_resource_details() throws IOException {
        assertNotNull(response.getBody());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.getBody());
        assertTrue(jsonResponse.has("id"));
        assertTrue(jsonResponse.has("s3Url"));
        assertTrue(jsonResponse.get("id").isInt());
        assertTrue(jsonResponse.get("s3Url").isTextual());
        existingResourceId = jsonResponse.get("id").asInt();
    }

    @Given("a resource exists with a specific ID")
    public void a_resource_exists_with_a_specific_id() throws IOException {
        if (existingResourceId == null) {
            a_user_has_an_audio_file();
            the_user_uploads_the_audio_file_to_the_resource_svc();
            the_user_receives_the_resource_details();
        }
    }

    @When("the user requests the resource by ID from the resource-svc")
    public void the_user_requests_the_resource_by_id_from_the_resource_svc() {
        String resourceUrl = baseUrl + "/" + existingResourceId;
        response = restTemplate.getForEntity(resourceUrl, String.class);
    }

    @Then("the correct resource data is returned")
    public void the_correct_resource_data_is_returned() {
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @When("the user deletes the resource by ID from the resource-svc")
    public void the_user_deletes_the_resource_by_id_from_the_resource_svc() {
        String resourceUrl = baseUrl + "?id=" + existingResourceId;
        restTemplate.delete(resourceUrl);
        response = restTemplate.getForEntity(resourceUrl, String.class);
    }

    @Then("the resource is successfully deleted")
    public void the_resource_is_successfully_deleted() {
        assertEquals(200, response.getStatusCode().value());
    }
}
