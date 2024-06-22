package com.tananushka.song.svc.repository;

import com.tananushka.song.svc.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    Optional<Song> findByResourceId(Integer resourceId);

    Boolean existsByResourceId(Integer resourceId);

    List<Song> findAllByResourceIdIn(List<Integer> resourceIds);

    void deleteByResourceIdIn(List<Integer> resourceIds);
}
