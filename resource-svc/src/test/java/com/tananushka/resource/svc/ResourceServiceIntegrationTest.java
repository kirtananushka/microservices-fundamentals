package com.tananushka.resource.svc;

import com.tananushka.resource.svc.dto.ResourceResponse;
import com.tananushka.resource.svc.repository.ResourceRepository;
import com.tananushka.resource.svc.service.ResourceService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ResourceSvcApp.class})
@ActiveProfiles("integration-test")
@Testcontainers
public class ResourceServiceIntegrationTest {

    @Container
    public static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withServices(LocalStackContainer.Service.S3);

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    public static GenericContainer<?> activeMQContainer = new GenericContainer<>("rmohr/activemq:latest")
            .withExposedPorts(61616);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceRepository resourceRepository;

    private byte[] testAudioData;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        registry.add("cloud.aws.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("cloud.aws.region", localStack::getRegion);
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);

        registry.add("spring.activemq.broker-url", () -> "tcp://" + activeMQContainer.getHost() + ":" + activeMQContainer.getMappedPort(61616));
        registry.add("spring.activemq.user", () -> "admin");
        registry.add("spring.activemq.password", () -> "admin");
    }

    @BeforeAll
    static void beforeAll() {
        S3Client s3Client = S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                ))
                .region(Region.of(localStack.getRegion()))
                .build();

        s3Client.createBucket(b -> b.bucket("test-bucket"));
    }

    @BeforeEach
    void setUp() throws IOException {
        resourceRepository.deleteAll();
        testAudioData = Files.readAllBytes(Path.of("src/test/resources/audio/valid-audio-data.mp3"));
    }

    @Test
    void testSaveAndRetrieveResource() throws IOException {
        // Save resource
        ResourceResponse saveResponse = resourceService.saveResource(testAudioData);
        assertThat(saveResponse).isNotNull();
        assertThat(saveResponse.getS3Url()).startsWith("s3://test-bucket/");

        // Retrieve resource
        byte[] retrievedAudioData = resourceService.getResourceData(saveResponse.getId());
        assertThat(retrievedAudioData).isNotNull();
        assertThat(retrievedAudioData).isEqualTo(testAudioData);
    }
}