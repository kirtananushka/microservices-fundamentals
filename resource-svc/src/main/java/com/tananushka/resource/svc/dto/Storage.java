package com.tananushka.resource.svc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
    private Long id;

    @NotBlank(message = "Storage type is required")
    @Pattern(regexp = "STAGING|PERMANENT", message = "Storage type must be STAGING or PERMANENT")
    private String storageType;

    @NotBlank(message = "Bucket is required")
    @Pattern(regexp = "^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$", message = "Bucket name must be between 3 and 63 characters long, can only contain lowercase letters, numbers, dots, and hyphens, and must begin and end with a letter or number")
    private String bucket;

    @NotBlank(message = "Path is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-][a-zA-Z0-9_/-]{4,14}$", message = "Path must be 5-15 characters long, contain only letters, numbers, hyphens, underscores, or forward slashes, and cannot begin with a forward slash")
    @Size(min = 5, max = 15, message = "Path must be between 5 and 15 characters long")
    private String path;
}
