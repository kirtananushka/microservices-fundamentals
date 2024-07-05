package com.tananushka.resource.proc.component.test;

import com.tananushka.resource.proc.client.ResourceSvcClient;
import com.tananushka.resource.proc.client.SongSvcClient;
import com.tananushka.resource.proc.service.Mp3MetadataService;
import com.tananushka.resource.proc.service.ResourceProcessorService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.support.RetryTemplate;

@TestConfiguration
public class ResourceProcComponentTestConfig {

    @MockBean
    public ResourceSvcClient resourceSvcClient;

    @MockBean
    public SongSvcClient songSvcClient;

    @MockBean
    public Mp3MetadataService mp3MetadataService;

    @MockBean
    private JmsTemplate jmsTemplate;

    @Bean
    public RetryTemplate retryTemplate() {
        return new RetryTemplate();
    }

    @Bean
    public ResourceProcessorService resourceProcessorService(RetryTemplate retryTemplate) {
        return new ResourceProcessorService(songSvcClient, resourceSvcClient, mp3MetadataService, retryTemplate);
    }
}