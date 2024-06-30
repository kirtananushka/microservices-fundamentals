package com.tananushka.song.svc.mapper;

import com.tananushka.song.svc.dto.MetadataRequest;
import com.tananushka.song.svc.dto.MetadataResponse;
import com.tananushka.song.svc.dto.SongIdResponse;
import com.tananushka.song.svc.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toEntity(MetadataRequest metadataRequest) {
        Song song = new Song();
        song.setResourceId(metadataRequest.getId());
        song.setArtist(metadataRequest.getArtist());
        song.setName(metadataRequest.getName());
        song.setAlbum(metadataRequest.getAlbum());
        song.setYear(metadataRequest.getYear());
        song.setDuration(metadataRequest.getDuration());
        return song;
    }

    public MetadataResponse toResponse(Song song) {
        MetadataResponse response = new MetadataResponse();
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
        response.setResourceId(Math.toIntExact(song.getResourceId()));
        return response;
    }
}