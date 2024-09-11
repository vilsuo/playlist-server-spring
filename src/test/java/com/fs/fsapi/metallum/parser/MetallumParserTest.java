package com.fs.fsapi.metallum.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.ArtistTitleSearchResult;
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
        MetallumFileHelper.readTitleWithLyricsFile()
      );

      assertEquals(expected.size(), actual.size());
      for (int i = 0; i < expected.size(); i++) {
        assertEquals(expected.get(i).getId(), actual.get(i).getId());
        assertEquals(expected.get(i).getTitle(), actual.get(i).getTitle());
        assertEquals(expected.get(i).getDuration(), actual.get(i).getDuration());
      }
    }
  }
}
