package com.tananushka.resource.proc.service;

import com.tananushka.resource.proc.client.SongClient;
import com.tananushka.resource.proc.dto.SongRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
@Slf4j
public class Mp3MetadataService {

    private final SongClient songClient;

    public void processMp3(byte[] mp3Data) {
        Metadata metadata = extractMetadata(mp3Data);
        SongRequest songRequest = createSongRequest(metadata);
        songClient.saveMetadata(songRequest);
    }

    private Metadata extractMetadata(byte[] mp3Data) {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream input = new ByteArrayInputStream(mp3Data)) {
            new Mp3Parser().parse(input, handler, metadata, new ParseContext());
        } catch (IOException | org.xml.sax.SAXException | org.apache.tika.exception.TikaException e) {
            log.error("Failed to extract metadata", e);
        }
        return metadata;
    }

    private SongRequest createSongRequest(Metadata metadata) {
        SongRequest request = new SongRequest();
        request.setArtist(metadata.get("xmpDM:artist"));
        request.setName(metadata.get("title"));
        request.setAlbum(metadata.get("xmpDM:album"));
        request.setYear(metadata.get("xmpDM:releaseDate"));
        request.setDuration(metadata.get("xmpDM:duration"));
        return request;
    }
}
