package com.tananushka.song.svc.controller;

import com.tananushka.song.svc.dto.MetadataRequest;
import com.tananushka.song.svc.dto.MetadataResponse;
import com.tananushka.song.svc.dto.SongIdResponse;
import com.tananushka.song.svc.service.SongService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@AllArgsConstructor
@Slf4j
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<SongIdResponse> addSong(@Valid @RequestBody MetadataRequest metadataRequest) {
        SongIdResponse response = songService.save(metadataRequest);
        log.info("Song saved: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataResponse> getSongByResourceId(@PathVariable Integer id) {
        MetadataResponse metadataResponse = songService.findByResourceId(id);
        return ResponseEntity.ok(metadataResponse);
    }

    @GetMapping
    public ResponseEntity<List<MetadataResponse>> getAllSongs() {
        List<MetadataResponse> metadataResponses = songService.findAll();
        return ResponseEntity.ok(metadataResponses);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteSongsById(@RequestParam String id) {
        List<Integer> deletedIds = songService.deleteSongsByResourceId(id);
        Map<String, List<Integer>> response = Collections.singletonMap("ids", deletedIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllSongs() {
        songService.deleteAllSongs();
        return ResponseEntity.noContent().build();
    }
}
