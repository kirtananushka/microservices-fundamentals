package com.tananushka.song.svc.mapper;

import com.tananushka.song.svc.dto.SongIdResponse;
import com.tananushka.song.svc.dto.SongRequest;
import com.tananushka.song.svc.dto.SongResponse;
import com.tananushka.song.svc.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toEntity(SongRequest songRequest) {
        Song song = new Song();
        song.setResourceId(songRequest.getId());
        song.setArtist(songRequest.getArtist());
        song.setName(songRequest.getName());
        song.setAlbum(songRequest.getAlbum());
        song.setYear(songRequest.getYear());
        song.setDuration(songRequest.getDuration());
        return song;
    }

    public SongResponse toResponse(Song song) {
        SongResponse response = new SongResponse();
        response.setId(Math.toIntExact(song.getId()));
        response.setName(song.getName());
        response.setArtist(song.getArtist());
        response.setAlbum(song.getAlbum());
        response.setDuration(song.getDuration());
        response.setResourceId(song.getResourceId());
        response.setYear(song.getYear());
        return response;
    }

    public SongIdResponse toIdResponse(Song song) {
        SongIdResponse response = new SongIdResponse();
        response.setId(Math.toIntExact(song.getId()));
        return response;
    }
}