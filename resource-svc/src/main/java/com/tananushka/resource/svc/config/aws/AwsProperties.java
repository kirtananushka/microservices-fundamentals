package com.tananushka.resource.svc.config.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
    private String endpoint;
    private String region;
    private S3Properties s3;
    private Credentials credentials;

    @Getter
    @Setter
    public static class S3Properties {
        private String bucket;
    }

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
}
