package com.tananushka.resource.proc.dto;

import lombok.Data;

@Data
public class MetadataRequest {
    private Integer id;
    private String artist;
    private String name;
    private String album;
    private String year;
    private String duration;
}
