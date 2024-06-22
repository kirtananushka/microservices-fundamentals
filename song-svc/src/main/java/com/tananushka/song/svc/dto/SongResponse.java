package com.tananushka.song.svc.dto;

import lombok.Data;

@Data
public class SongResponse {

    private Integer id;

    private String name;

    private String artist;

    private String album;

    private String duration;

    private Integer resourceId;

    private String year;
}
