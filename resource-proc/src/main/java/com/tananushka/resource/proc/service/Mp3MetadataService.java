package com.tananushka.resource.proc.service;

import com.tananushka.resource.proc.dto.MetadataRequest;
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

    public Metadata extractMetadata(byte[] mp3Data) {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream input = new ByteArrayInputStream(mp3Data)) {
            new Mp3Parser().parse(input, handler, metadata, new ParseContext());
        } catch (IOException | org.xml.sax.SAXException | org.apache.tika.exception.TikaException e) {
            log.error("Failed to extract metadata", e);
        }
        return metadata;
    }

    public MetadataRequest createSongRequest(Metadata metadata) {
        MetadataRequest request = new MetadataRequest();
        request.setArtist(metadata.get("xmpDM:artist"));
        request.setName(metadata.get("title"));
        request.setAlbum(metadata.get("xmpDM:album"));
        request.setYear(metadata.get("xmpDM:releaseDate"));
        request.setDuration(formatDuration(metadata.get("xmpDM:duration")));
        return request;
    }

    private String formatDuration(String durationInMilliseconds) {
        if (durationInMilliseconds != null && !durationInMilliseconds.isEmpty()) {
            double milliseconds = Double.parseDouble(durationInMilliseconds);
            int totalSeconds = (int) (milliseconds / 1000);
            int minutes = totalSeconds / 60;
            int remainingSeconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, remainingSeconds);
        }
        return null;
    }
}
