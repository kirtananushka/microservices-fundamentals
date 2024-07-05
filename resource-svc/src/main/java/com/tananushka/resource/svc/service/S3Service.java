package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.config.aws.AwsProperties;
import com.tananushka.resource.svc.exception.ResourceServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public String uploadToS3(byte[] data) {
        String key = UUID.randomUUID() + ".mp3";
        try {
            File file = File.createTempFile(key, null);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucket())
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));
            return "s3://" + awsProperties.getS3().getBucket() + "/" + key;
        } catch (IOException e) {
            throw new ResourceServiceException("Failed to upload file to S3", "500");
        }
    }

    public byte[] downloadFromS3(String s3Location) {
        String bucketName = awsProperties.getS3().getBucket();
        String key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
    }

    public void deleteFromS3(String s3Location) {
        String bucketName = awsProperties.getS3().getBucket();
        String key = s3Location.substring(s3Location.lastIndexOf("/") + 1);
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
        } catch (Exception e) {
            throw new ResourceServiceException("Failed to delete file from S3: " + s3Location, "500", e);
        }
    }
}
