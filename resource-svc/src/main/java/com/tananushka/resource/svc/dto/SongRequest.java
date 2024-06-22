package com.tananushka.resource.svc.dto;

import lombok.Data;

@Data
public class SongRequest {

    private Integer id;

    private String artist;

    private String name;

    private String album;

    private String year;

    private String duration;
}
