package com.fs.fsapi.metallum.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

@SpringBootTest(classes = { MetallumParser.class })
public class MetallumParserTest {

  @Autowired
  private MetallumParser parser;

  @Nested
  @DisplayName("getSearchResult")
  public class SearchResult {

    private final ArtistTitleSearchResult expected = MetallumFileHelper.searchResult;

    @Test
    public void shouldReturnSearchResultTest() {
      ArtistTitleSearchResponse response = MetallumFileHelper.searchResponse;
      ArtistTitleSearchResult actual = parser.getSearchResult(
        response, null, null
      );

      assertEquals(expected.getArtist(), actual.getArtist());
      assertEquals(expected.getArtistHref(), actual.getArtistHref());
      assertEquals(expected.getArtistId(), actual.getArtistId());
      
      assertEquals(expected.getTitle(), actual.getTitle());
      assertEquals(expected.getTitleHref(), actual.getTitleHref());
      assertEquals(expected.getTitleId(), actual.getTitleId());

      assertEquals(expected.getReleaseType(), actual.getReleaseType());
    }
  }
  
  @Nested
  @DisplayName("parseSongs")
  public class ParseSongs {

    private final List<SongResult> expected = MetallumFileHelper.songsWithLyrics;

    @Test
    public void shouldParseSongsTest() throws IOException {
      List<SongResult> actual = parser.parseSongs(
        MetallumFileHelper.readTitlePageWithLyricsFile()
      );

      assertEquals(expected.size(), actual.size());
      for (int i = 0; i < expected.size(); i++) {
        assertEquals(expected.get(i).getId(), actual.get(i).getId());
        assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
        assertEquals(expected.get(i).getDuration(), actual.get(i).getDuration());
      }
    }
  }

  @Nested
  @DisplayName("parseLyrics")
  public class ParseLyrics {

    @Nested
    @DisplayName("when song lyrics are available")
    public class Available {

      final LyricsResult expected = MetallumFileHelper.lyrics;
      LyricsResult actual;

      @BeforeEach
      public void parse() throws IOException {
        actual = parser.parseLyrics(MetallumFileHelper.readLyricsFile());
      }

      @Test
      public void shouldNotHaveErrorTest() throws IOException {
        assertTrue(actual.getError().isEmpty());
      }

      @Test
      public void shouldHaveTheSongLyricsTest() throws IOException {
        List<String> expectedLyrics = expected.getLyrics();
        List<String> actualLyrics = actual.getLyrics();
  
        assertEquals(expectedLyrics.size(), actualLyrics.size());
        for (int i = 0; i < actual.getLyrics().size(); ++i) {
          assertEquals(expectedLyrics.get(i), actualLyrics.get(i));
        }
      }
    }

    @Nested
    @DisplayName("when song lyrics are not available")
    public class NotAvailable {

      final LyricsResult expected = MetallumFileHelper.lyricsNotAvailable;
      LyricsResult actual;

      @BeforeEach
      public void parse() throws IOException {
        actual = parser.parseLyrics(
          MetallumFileHelper.readLyricsNotAvailableFile()
        );
      }

      @Test
      public void shouldHaveErrorTest() throws IOException {
        final String error = actual.getError();
        assertFalse(error.isEmpty());
        assertEquals("Lyrics not available", error);
      }

      @Test
      public void shouldNotHaveAnySongLyricsTest() throws IOException {
        List<String> expectedLyrics = expected.getLyrics();
        List<String> actualLyrics = actual.getLyrics();
  
        assertTrue(expectedLyrics.isEmpty());
        assertTrue(actualLyrics.isEmpty());
      }
    }

    @Nested
    @DisplayName("when song is instrumental")
    public class Instrumental {

      final LyricsResult expected = MetallumFileHelper.lyricsInstrumental;
      LyricsResult actual;

      @BeforeEach
      public void parse() throws IOException {
        actual = parser.parseLyrics(
          MetallumFileHelper.readLyricsInstrumentalFile()
        );
      }

      @Test
      public void shouldHaveErrorTest() throws IOException {
        final String error = actual.getError();
        assertFalse(error.isEmpty());
        assertEquals("Instrumental", error);
      }

      @Test
      public void shouldNotHaveAnySongLyricsTest() throws IOException {
        List<String> expectedLyrics = expected.getLyrics();
        List<String> actualLyrics = actual.getLyrics();
  
        assertTrue(expectedLyrics.isEmpty());
        assertTrue(actualLyrics.isEmpty());
      }
    }
  }
}
