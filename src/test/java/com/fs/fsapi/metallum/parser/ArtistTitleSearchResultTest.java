package com.fs.fsapi.metallum.parser;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.fs.fsapi.helpers.MetallumFileHelper;

public class ArtistTitleSearchResultTest {
  
  private final ArtistTitleSearchResult searchResult = MetallumFileHelper.SEARCH_RESULT;

  @Test
  public void shouldHaveExpectedTitleHrefTest() {
    assertEquals(
      "https://www.metal-archives.com/albums/Adramelech/Human_Extermination/73550",
      searchResult.getTitleHref()
    );
  }

  @Test
  public void shouldHaveExpectedArtistHrefTest() {
    assertEquals(
      "https://www.metal-archives.com/bands/Adramelech/2426", 
      searchResult.getArtistHref()
    );
  }

  @Test
  public void shouldHaveExpectedTitleTest() {
    assertEquals("Human Extermination", searchResult.getTitle());
  }

  @Test
  public void shouldHaveExpectedArtistTest() {
    assertEquals("Adramelech", searchResult.getArtist());
  }

  @Test
  public void shouldHaveExpectedTitleIdTest() {
    assertEquals("73550", searchResult.getTitleId());
  }

  @Test
  public void shouldHaveExpectedArtistIdTest() {
    assertEquals("2426", searchResult.getArtistId());
  }

  @Test
  public void shouldHaveExpectedReleaseTyTest() {
    assertEquals("Demo", searchResult.getReleaseType());
  }
}
