package com.fs.fsapi.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fs.fsapi.metallum.parser.SongResult;
import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

public class MetallumFileHelper {

  private static final String TEST_FILES_LOCATION = "src/test/data/metallum";

  // files
  private static final String SEARCH_RESPONSE_FILE = "search.json";
  private static final String TITLE_WITH_LYRICS_RESPONSE_FILE = "title-with-lyrics.html";
  private static final String TITLE_WITHOUT_LYRICS_RESPONSE_FILE = "title-without-lyrics.html";

  public static String readSearchResponseFile() throws IOException {
    return readFileAsString(SEARCH_RESPONSE_FILE);
  }

  public static String readTitleWithLyricsFile() throws IOException {
    return readFileAsString(TITLE_WITH_LYRICS_RESPONSE_FILE);
  }

  public static String readTitleWithoutLyricsFile() throws IOException {
    return readFileAsString(TITLE_WITHOUT_LYRICS_RESPONSE_FILE);
  }

  private static String readFileAsString(String filename) throws IOException {
    File initialFile = new File(TEST_FILES_LOCATION + "/" + filename);
    return FileUtils.readFileToString(initialFile, StandardCharsets.UTF_8);
  }


  public static final ArtistTitleSearchResponse r1 = new ArtistTitleSearchResponse(
    "",
    1,
    1,
    Arrays.asList(
      new AaDataValue(List.of(
        "<a href=\"https://www.metal-archives.com/bands/Adramelech/2426\" title=\"Adramelech (FI)\">Adramelech</a>",
        "<a href=\"https://www.metal-archives.com/albums/Adramelech/Human_Extermination/73550\">Human Extermination</a> <!-- 16.817602 -->",
        "Demo"
      ))
    )
  );

  public static final List<SongResult> songsWithLyrics = List.of(
    new SongResult("57360", "Heroes in Godly Blaze", "04:11"),
    new SongResult("57361", "Psychostasia", "04:06"),
    new SongResult("57362", "Seance of Shamans", "03:26"),
    new SongResult("57363", "The Book of the Worm", "06:11"),
    new SongResult("57364", "Thoth (Lord of Holy Words)", "03:10"),
    new SongResult("57365", "Mythic Descendant", "04:19"),
    new SongResult("57366", "As the Gods Succumbed", "05:02"),
    new SongResult("57367", "Across the Gray Waters", "03:59")
  );

  public static final List<SongResult> songsWithouLyrics = List.of(
    new SongResult("160574A", "Mortal God", "02:37"),
    new SongResult("160575A", "Grip of Darkness", "03:23"),
    new SongResult("160576A", "Ancestral Souls", "02:51"),
    new SongResult("160577A", "Dreamdeath", "02:23")
  );
}
