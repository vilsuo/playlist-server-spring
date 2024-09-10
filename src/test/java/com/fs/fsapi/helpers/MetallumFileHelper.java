package com.fs.fsapi.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fs.fsapi.metallum.response.AaDataValue;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;

public class MetallumFileHelper {

  private static final String TEST_FILES_LOCATION = "src/test/data/metallum";

  // files
  private static final String SEARCH_RESPONSE_FILE = "search.json";

  public static String readSearchResponseFile() throws IOException {
    return readFileAsString(SEARCH_RESPONSE_FILE);
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
}
