package com.tananushka.song.svc.dto;

import lombok.Data;

@Data
public class MetadataResponse {
    private Integer resourceId;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
}
