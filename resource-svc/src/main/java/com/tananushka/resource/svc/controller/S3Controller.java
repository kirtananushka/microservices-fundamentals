package com.tananushka.resource.svc.controller;

import com.tananushka.resource.svc.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/buckets")
    public ResponseEntity<List<String>> listBuckets() {
        List<String> buckets = s3Service.listBuckets();
        return ResponseEntity.ok(buckets);
    }
}
