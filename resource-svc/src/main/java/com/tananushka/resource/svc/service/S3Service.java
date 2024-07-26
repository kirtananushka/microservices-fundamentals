package com.tananushka.resource.svc.service;

import com.tananushka.resource.svc.dto.Storage;
import com.tananushka.resource.svc.exception.ResourceServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client s3Client;

    public String uploadToS3(byte[] data, Storage storage) {
        String bucketName = storage.getBucket();
        String path = storage.getPath();
        String storageType = storage.getStorageType();

        log.debug("Attempting to upload file to S3. Storage Type: {}, Bucket: {}, Path: {}", storageType, bucketName, path);
        ensureBucketExists(bucketName);

        String key = path + "/" + UUID.randomUUID() + ".mp3";
        try {
            File file = File.createTempFile(key, null);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));
            log.info("File uploaded to {} S3 bucket successfully. S3 Location: s3://{}/{}", storageType, bucketName, key);
            return "s3://" + bucketName + "/" + key;
        } catch (IOException e) {
            throw new ResourceServiceException("Failed to upload file to " + storageType + " S3 " + bucketName, "500");
        }
    }

    public byte[] downloadFromS3(String s3Location) {
        String bucketName = s3Location.split("/")[2];
        String key = s3Location.substring(s3Location.indexOf("/", 5) + 1);

        log.debug("Attempting to download file from S3. Bucket: {}, Key: {}", bucketName, key);
        ensureBucketExists(bucketName);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        log.debug("File downloaded from S3 successfully. S3 Location: {}", s3Location);
        return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
    }

    public void deleteFromS3(String s3Location, String storageType) {
        String bucketName = s3Location.split("/")[2];
        String key = s3Location.substring(s3Location.indexOf("/", 5) + 1);

        log.debug("Attempting to delete file from S3. Storage Type: {}, Bucket: {}, Key: {}", storageType, bucketName, key);
        ensureBucketExists(bucketName);
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
            log.info("File removed from the {} bucket successfully. S3 Location: s3://{}/{}", storageType, bucketName, key);
        } catch (Exception e) {
            throw new ResourceServiceException("Failed to delete file from " + storageType + " bucket: " + s3Location, "500", e);
        }
    }

    public List<String> listBuckets() {
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
        List<String> bucketNames = listBucketsResponse.buckets().stream()
                .map(Bucket::name)
                .toList();
        log.info("Retrieved {} buckets", bucketNames.size());
        return bucketNames;
    }
    
    private void ensureBucketExists(String bucketName) {
        log.debug("Ensuring bucket exists: {}", bucketName);
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.debug("Bucket exists: {}", bucketName);
        } catch (NoSuchBucketException e) {
            log.warn("Bucket does not exist, creating bucket: {}", bucketName);
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            waitUntilBucketExists(bucketName);
        }
    }

    private void waitUntilBucketExists(String bucketName) {
        int attempts = 0;
        while (attempts < 10) {
            try {
                Thread.sleep(2000);
                s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
                log.info("Bucket confirmed: {}", bucketName);
                return;
            } catch (NoSuchBucketException e) {
                attempts++;
                log.warn("Bucket not yet created, retrying... Attempt: {}", attempts);
            } catch (InterruptedException e) {
                log.error("Thread interrupted while waiting for bucket creation: {}", bucketName);
                Thread.currentThread().interrupt();
                throw new ResourceServiceException("Thread interrupted while waiting for bucket creation: " + bucketName, "500", e);
            }
        }
        throw new ResourceServiceException("Failed to create bucket: " + bucketName, "500");
    }
}
