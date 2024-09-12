package com.fs.fsapi.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.metallum.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.parser.LyricsResult;
import com.fs.fsapi.metallum.parser.SongResult;
import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

public class MetallumFileHelper {

  private static final String METALLUM_TEST_FILES_LOCATION = "src/test/data/metallum";

  // files
  private static final String SEARCH_RESPONSE_FILE = "search.json";

  private static final String TITLE_WITH_LYRICS_RESPONSE_FILE = "title-page-with-lyrics.html";
  private static final String TITLE_WITHOUT_LYRICS_RESPONSE_FILE = "title-page-without-lyrics.html";

  private static final String LYRICS_RESPONSE_FILE = "lyrics-57360.html";
  private static final String LYRICS_NOT_AVAILABLE_RESPONSE_FILE = "lyrics-not-available.html";
  private static final String LYRICS_INSTRUMENTAL_RESPONSE_FILE = "lyrics-instrumental.html";

  // advanced search results
  public static String readSearchResponseFile() throws IOException {
    return readFileAsString(SEARCH_RESPONSE_FILE);
  }

  // title pages
  public static String readTitlePageWithLyricsFile() throws IOException {
    return readFileAsString(TITLE_WITH_LYRICS_RESPONSE_FILE);
  }

  public static String readTitlePageWithoutLyricsFile() throws IOException {
    return readFileAsString(TITLE_WITHOUT_LYRICS_RESPONSE_FILE);
  }

  // lyrics
  public static String readLyricsFile() throws IOException {
    return readFileAsString(LYRICS_RESPONSE_FILE);
  }

  public static String readLyricsNotAvailableFile() throws IOException {
    return readFileAsString(LYRICS_NOT_AVAILABLE_RESPONSE_FILE);
  }

  public static String readLyricsInstrumentalFile() throws IOException {
    return readFileAsString(LYRICS_INSTRUMENTAL_RESPONSE_FILE);
  }

  private static String readFileAsString(String filename) throws IOException {
    File initialFile = new File(METALLUM_TEST_FILES_LOCATION + "/" + filename);
    return FileUtils.readFileToString(initialFile, StandardCharsets.UTF_8);
  }

  // EXPECTED RESULTS

  public static final ArtistTitleSearchResponse searchResponse = new ArtistTitleSearchResponse(
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

  public static final ArtistTitleSearchResult searchResult = new ArtistTitleSearchResult(
    new LinkElement(ElementHelper.createLinkTypeElement(
      "Adramelech", 
      "https://www.metal-archives.com/bands/Adramelech/2426"
    )), 
    new LinkElement(ElementHelper.createLinkTypeElement(
      "Human Extermination",
      "https://www.metal-archives.com/albums/Adramelech/Human_Extermination/73550"
    )), 
    "Demo"
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

  public static final List<SongResult> songsWithoutLyrics = List.of(
    new SongResult("160574A", "Mortal God", "02:37"),
    new SongResult("160575A", "Grip of Darkness", "03:23"),
    new SongResult("160576A", "Ancestral Souls", "02:51"),
    new SongResult("160577A", "Dreamdeath", "02:23")
  );

  public static final LyricsResult lyrics = new LyricsResult(List.of(
    "Odin - the god of nocturnal storms",
    "ranged the sky with his horse",
    "deciding man's fate",
    "",
    "When there was a battle on earth",
    "Odin sent his valkyries",
    "to mingle with the combatants and determine",
    "which warriors should fall",
    "",
    "And they awarded - victory and glory",
    "to those combatants - who won their favour",
    "After the bloodbath - they returned to Valhalla",
    "announced to Odin - those who would join the troop",
    "",
    "In the splendour of his holyness - he rarely appeared",
    "but in a disguise - he travelled the world",
    "creating warriors - so strong and glorious",
    "from ordinary men",
    "grew up so splendid heroes",
    "",
    "Heroes in godly blaze",
    "",
    "He knew the spells - which cured the illness",
    "magic formulas - which broke the chains",
    "Those which rendered - weapons so powerless",
    "dead could speak, waves could rose",
    "- by his magic spells"
  ));

  public static final LyricsResult lyricsNotAvailable = new LyricsResult("Lyrics not available");
  public static final LyricsResult lyricsInstrumental = new LyricsResult("Instrumental");
}
