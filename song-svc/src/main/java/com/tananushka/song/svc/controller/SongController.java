package com.tananushka.song.svc.controller;

import com.tananushka.song.svc.dto.SongIdResponse;
import com.tananushka.song.svc.dto.SongRequest;
import com.tananushka.song.svc.dto.SongResponse;
import com.tananushka.song.svc.service.SongService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<SongIdResponse> addSong(@Valid @RequestBody SongRequest songRequest) {
        SongIdResponse response = songService.save(songRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongByResourceId(@PathVariable Integer id) {
        SongResponse songResponse = songService.findByResourceId(id);
        return ResponseEntity.ok(songResponse);
    }

    @GetMapping
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        List<SongResponse> songResponses = songService.findAll();
        return ResponseEntity.ok(songResponses);
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
