package com.tananushka.song.svc.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongRequest {

    @Positive(message = "ID must be a positive number")
    private Integer id;

    @Size(max = 255, message = "Artist name must be less than or equal to 255 characters")
    private String artist;

    @Size(max = 255, message = "Song name must be less than or equal to 255 characters")
    private String name;

    @Size(max = 255, message = "Album name must be less than or equal to 255 characters")
    private String album;

    @Pattern(regexp = "[0-9]{4}", message = "Year must be in a YYYY format")
    private String year;

    @Pattern(regexp = "[0-9]{2}:[0-5][0-9]", message = "Duration must be in the format MM:SS")
    private String duration;
}
