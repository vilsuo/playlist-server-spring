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

@SpringBootTest(classes = { MetallumParser.class })
public class MetallumParserTest {

  @Autowired
  private MetallumParser parser;
  
  @Nested
  @DisplayName("parseSongs")
  public class Songs {

    private final List<SongResult> expected = MetallumFileHelper.songsWithLyrics;

    @Test
    public void shoulParseSongsTest() throws IOException {
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
