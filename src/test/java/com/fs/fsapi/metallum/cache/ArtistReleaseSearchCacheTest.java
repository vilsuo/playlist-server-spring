package com.fs.fsapi.metallum.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fs.fsapi.bookmark.parser.LinkElement;
import com.fs.fsapi.helpers.ElementHelper;
import com.fs.fsapi.metallum.ArtistReleaseSearchResult;

@SpringBootTest(classes = { ArtistReleaseSearchCache.class })
public class ArtistReleaseSearchCacheTest {
  
  @Autowired
  private ArtistReleaseSearchCache cache;

  private final String text1 = "Adramelech";
  private final String href1 = "https://www.metal-archives.com/bands/Adramelech/2426";

  private final String text2 = "Human Extermination";
  private final String href2 = "https://www.metal-archives.com/albums/Adramelech/Human_Extermination/73550";

  private final String type = "Demo";

  ArtistReleaseSearchResult value = new ArtistReleaseSearchResult(
    new LinkElement(ElementHelper.createLinkTypeElement(text1, href1, null)), 
    new LinkElement(ElementHelper.createLinkTypeElement(text2, href2, null)), 
    type
  );

  @BeforeEach
  public void clear() {
    cache.clear();
  }

  @Test
  public void shouldNotContainMappingInitiallyTest() {
    assertTrue(cache.get(text1, text2).isEmpty());
  }

  @Test
  public void shouldContainMappingAfterPuttingTest() {
    cache.put(text1, text2, value);

    Optional<ArtistReleaseSearchResult> opt = cache.get(text1, text2);
    assertTrue(opt.isPresent());

    ArtistReleaseSearchResult result = opt.get();

    assertEquals(href1, result.getArtistHref());
    assertEquals(text1, result.getArtist());

    assertEquals(href2, result.getReleaseHref());
    assertEquals(text2, result.getRelease());

    assertEquals(type, result.getReleaseType());
  }

  @Test
  public void shouldNotContainMappingAfterClearingTest() {
    cache.put(text1, text2, value);

    cache.clear();

    assertTrue(cache.get(text1, text2).isEmpty());
  }
}
