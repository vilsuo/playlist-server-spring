package com.fs.fsapi.metallum.parser;

import java.io.IOException;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import com.fs.fsapi.helpers.MetallumFileHelper;

@JsonTest
public class ArtistTitleSearchResultSerializationTest {
  
  @Autowired
  private JacksonTester<ArtistTitleSearchResult> jacksonTester;

  private final ArtistTitleSearchResult searchResult = MetallumFileHelper.SEARCH_RESULT;

  private final String expected = "{"
  + "\"artist\":\"Adramelech\","
  + "\"artistHref\":\"https://www.metal-archives.com/bands/Adramelech/2426\","
  + "\"artistId\":\"2426\","
  + "\"title\":\"Human Extermination\","
  + "\"titleHref\":\"https://www.metal-archives.com/albums/Adramelech/Human_Extermination/73550\","
  + "\"titleId\":\"73550\","
  + "\"releaseType\":\"Demo\""
  + "}";

  @Test
  public void shouldSerializeTest() throws IOException, JSONException {
    JsonContent<ArtistTitleSearchResult> jsonContent = jacksonTester.write(searchResult);

    final String actual = jsonContent.getJson();
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
}
