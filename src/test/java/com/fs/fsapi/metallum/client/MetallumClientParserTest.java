package com.fs.fsapi.metallum.client;

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
import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

@SpringBootTest(classes = { MetallumClientParser.class })
public class MetallumClientParserTest {

  @Autowired
  private MetallumClientParser parser;

  @Nested
  @DisplayName("getSearchResult")
  public class SearchResult {

    private final ArtistTitleSearchResult expected = MetallumFileHelper.SEARCH_RESULT;

    @Test
    public void shouldReturnSearchResultTest() {
      final ArtistTitleSearchResponse response = MetallumFileHelper.SEARCH_RESPONSE;
      final ArtistTitleSearchResult actual = parser
        .getSearchResult(response, null, null);

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

    private final List<SongResult> expected = MetallumFileHelper.SONGS_RESULT_WITH_LYRICS;

    @Test
    public void shouldParseSongsTest() throws IOException {
      final List<SongResult> actual = parser.parseSongs(
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

      final LyricsResult expected = MetallumFileHelper.LYRICS_RESULT;
      LyricsResult actual;

      @BeforeEach
      public void parse() throws IOException {
        actual = parser.parseLyrics(MetallumFileHelper.readLyricsFile());
      }

      @Test
      public void shouldNotHaveMessageTest() throws IOException {
        assertTrue(actual.getMessage().isEmpty());
      }

      @Test
      public void shouldHaveTheSongLyricsTest() throws IOException {
        final List<String> expectedLyrics = expected.getLines();
        final List<String> actualLyrics = actual.getLines();
  
        assertEquals(expectedLyrics.size(), actualLyrics.size());
        for (int i = 0; i < actual.getLines().size(); ++i) {
          assertEquals(expectedLyrics.get(i), actualLyrics.get(i));
        }
      }
    }

    @Nested
    @DisplayName("when song lyrics are not available")
    public class NotAvailable {

      final LyricsResult expected = MetallumFileHelper.LYRICS_RESULT_NOT_AVAILABLE;
      LyricsResult actual;

      @BeforeEach
      public void parse() throws IOException {
        actual = parser.parseLyrics(
          MetallumFileHelper.readLyricsNotAvailableFile()
        );
      }

      @Test
      public void shouldHaveMessageTest() throws IOException {
        final String message = actual.getMessage();
        assertFalse(message.isEmpty());
        assertEquals("Lyrics not available", message);
      }

      @Test
      public void shouldNotHaveAnySongLyricsTest() throws IOException {
        final List<String> expectedLyrics = expected.getLines();
        final List<String> actualLyrics = actual.getLines();
  
        assertTrue(expectedLyrics.isEmpty());
        assertTrue(actualLyrics.isEmpty());
      }
    }

    @Nested
    @DisplayName("when song is instrumental")
    public class Instrumental {

      final LyricsResult expected = MetallumFileHelper.LYRICS_RESULT_INSTRUMENTAL;
      LyricsResult actual;

      @BeforeEach
      public void parse() throws IOException {
        actual = parser.parseLyrics(MetallumFileHelper.readLyricsInstrumentalFile());
      }

      @Test
      public void shouldHaveMessageTest() throws IOException {
        final String message = actual.getMessage();
        assertFalse(message.isEmpty());
        assertEquals("Instrumental", message);
      }

      @Test
      public void shouldNotHaveAnySongLyricsTest() throws IOException {
        final List<String> expectedLyrics = expected.getLines();
        final List<String> actualLyrics = actual.getLines();
  
        assertTrue(expectedLyrics.isEmpty());
        assertTrue(actualLyrics.isEmpty());
      }
    }
  }
}
