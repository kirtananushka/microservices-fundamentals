package com.tananushka.resource.proc.dto;

import lombok.Data;

@Data
public class SongRequest {
    private String artist;
    private String name;
    private String album;
    private String year;
    private String duration;
}
