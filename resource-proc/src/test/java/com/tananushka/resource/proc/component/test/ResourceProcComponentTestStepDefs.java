package com.tananushka.resource.proc.component.test;

import com.tananushka.resource.proc.ResourceProcApp;
import com.tananushka.resource.proc.client.ResourceSvcClient;
import com.tananushka.resource.proc.client.SongSvcClient;
import com.tananushka.resource.proc.dto.MetadataRequest;
import com.tananushka.resource.proc.dto.SongIdResponse;
import com.tananushka.resource.proc.exception.ResourceProcessorException;
import com.tananushka.resource.proc.service.Mp3MetadataService;
import com.tananushka.resource.proc.service.ResourceProcessorService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration(classes = {ResourceProcApp.class, ResourceProcComponentTestConfig.class})
@ActiveProfiles("component-test")
public class ResourceProcComponentTestStepDefs {

    @Autowired
    private ResourceProcessorService resourceProcessorService;

    @Autowired
    private ResourceSvcClient resourceSvcClient;

    @Autowired
    private SongSvcClient songSvcClient;

    @Autowired
    private Mp3MetadataService mp3MetadataService;

    private Exception thrownException;
    private Message mockMessage;

    @BeforeEach
    public void setup() {
        mockMessage = Mockito.mock(TextMessage.class);
    }

    @Given("a new resource with ID {int} is available")
    public void a_new_resource_with_id_is_available(Integer id) throws JMSException {
        byte[] dummyAudioData = "dummy audio data".getBytes();
        ResponseEntity<byte[]> mockResponse = ResponseEntity.ok().body(dummyAudioData);
        when(resourceSvcClient.getAudioData(id)).thenReturn(mockResponse);

        Metadata dummyMetadata = new Metadata();
        dummyMetadata.set("title", "Test Song");
        when(mp3MetadataService.extractMetadata(any())).thenReturn(dummyMetadata);

        MetadataRequest dummyRequest = new MetadataRequest();
        dummyRequest.setName("Test Song");
        when(mp3MetadataService.createSongRequest(any())).thenReturn(dummyRequest);

        when(songSvcClient.saveMetadata(any())).thenReturn(new SongIdResponse(id));

        // Initialize the mock message
        mockMessage = Mockito.mock(TextMessage.class);
        when(mockMessage.getBody(Long.class)).thenReturn(Long.valueOf(id));
        when(mockMessage.getStringProperty("X-Trace-Id")).thenReturn("dummy-trace-id");
    }

    @When("the resource processor processes the resource")
    public void the_resource_processor_processes_the_resource() {
        try {
            resourceProcessorService.processResource(mockMessage);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the metadata is extracted and saved correctly")
    public void the_metadata_is_extracted_and_saved_correctly() {
        assertNull(thrownException, "No exception should be thrown");
        verify(resourceSvcClient).getAudioData(1);
        verify(mp3MetadataService).extractMetadata(any());
        verify(mp3MetadataService).createSongRequest(any());

        ArgumentCaptor<MetadataRequest> requestCaptor = ArgumentCaptor.forClass(MetadataRequest.class);
        verify(songSvcClient).saveMetadata(requestCaptor.capture());
        MetadataRequest capturedRequest = requestCaptor.getValue();
        assertEquals("Test Song", capturedRequest.getName());
    }

    @Given("a new resource with ID {int} is available but cannot be processed")
    public void a_new_resource_with_id_is_available_but_cannot_be_processed(Integer id) throws JMSException {
        when(resourceSvcClient.getAudioData(id)).thenThrow(new RuntimeException("Cannot process resource"));

        // Initialize the mock message
        mockMessage = Mockito.mock(TextMessage.class);
        when(mockMessage.getBody(Long.class)).thenReturn(Long.valueOf(id));
        when(mockMessage.getStringProperty("X-Trace-Id")).thenReturn("dummy-trace-id");
    }

    @When("the resource processor attempts to process the resource")
    public void the_resource_processor_attempts_to_process_the_resource() {
        try {
            resourceProcessorService.processResource(mockMessage);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("an error is logged and a ResourceProcessorException is thrown")
    public void an_error_is_logged_and_a_resource_processor_exception_is_thrown() {
        assertNotNull(thrownException);
        assertInstanceOf(ResourceProcessorException.class, thrownException);
        assertEquals("Failed to process resourceId=1", thrownException.getMessage());
    }
}
