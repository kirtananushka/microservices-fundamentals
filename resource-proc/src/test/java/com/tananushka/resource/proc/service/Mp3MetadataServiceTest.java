package com.tananushka.resource.proc.service;

import com.tananushka.resource.proc.dto.MetadataRequest;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Mp3MetadataServiceTest {

    @Mock
    private Mp3Parser mp3Parser;

    @InjectMocks
    private Mp3MetadataService mp3MetadataService;

    private byte[] mockMp3Data;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMp3Data = new byte[]{0, 1, 2, 3};
    }

    @Test
    public void testExtractMetadata_Success() throws IOException, SAXException, org.apache.tika.exception.TikaException {
        doNothing().when(mp3Parser).parse(any(InputStream.class), any(BodyContentHandler.class), any(Metadata.class), any(ParseContext.class));

        Metadata extractedMetadata = mp3MetadataService.extractMetadata(mockMp3Data);

        assertNotNull(extractedMetadata);
        verify(mp3Parser, times(1)).parse(any(InputStream.class), any(BodyContentHandler.class), any(Metadata.class), any(ParseContext.class));
    }

    @Test
    public void testExtractMetadata_Failure() throws IOException, SAXException, org.apache.tika.exception.TikaException {
        doThrow(IOException.class).when(mp3Parser).parse(any(InputStream.class), any(BodyContentHandler.class), any(Metadata.class), any(ParseContext.class));

        Metadata extractedMetadata = mp3MetadataService.extractMetadata(mockMp3Data);

        assertNotNull(extractedMetadata);
        verify(mp3Parser, times(1)).parse(any(InputStream.class), any(BodyContentHandler.class), any(Metadata.class), any(ParseContext.class));
    }

    @Test
    public void testCreateSongRequest() {
        Metadata metadata = new Metadata();
        metadata.set("xmpDM:artist", "Test Artist");
        metadata.set("title", "Test Title");
        metadata.set("xmpDM:album", "Test Album");
        metadata.set("xmpDM:releaseDate", "2024");
        metadata.set("xmpDM:duration", "180000");

        MetadataRequest metadataRequest = mp3MetadataService.createSongRequest(metadata);

        assertEquals("Test Artist", metadataRequest.getArtist());
        assertEquals("Test Title", metadataRequest.getName());
        assertEquals("Test Album", metadataRequest.getAlbum());
        assertEquals("2024", metadataRequest.getYear());
        assertEquals("03:00", metadataRequest.getDuration());
    }

    @Test
    public void testCreateSongRequest_WithInvalidDuration() {
        Metadata metadata = new Metadata();
        metadata.set("xmpDM:artist", "Test Artist");
        metadata.set("title", "Test Title");
        metadata.set("xmpDM:album", "Test Album");
        metadata.set("xmpDM:releaseDate", "2024");
        metadata.set("xmpDM:duration", "");

        MetadataRequest metadataRequest = mp3MetadataService.createSongRequest(metadata);

        assertEquals("Test Artist", metadataRequest.getArtist());
        assertEquals("Test Title", metadataRequest.getName());
        assertEquals("Test Album", metadataRequest.getAlbum());
        assertEquals("2024", metadataRequest.getYear());
        assertNull(metadataRequest.getDuration());
    }
}
