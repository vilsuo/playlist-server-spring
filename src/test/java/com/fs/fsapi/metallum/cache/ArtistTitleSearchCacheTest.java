package com.fs.fsapi.metallum.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.parser.ArtistTitleSearchResult;

@SpringBootTest(classes = { ArtistTitleSearchCache.class })
public class ArtistTitleSearchCacheTest {
  
  @Autowired
  private ArtistTitleSearchCache cache;

  private final ArtistTitleSearchResult expected = MetallumFileHelper.searchResult;
  private final String key1 = expected.getArtist();
  private final String key2 = expected.getTitle();

  @BeforeEach
  public void clear() {
    cache.clear();
  }

  @Test
  public void shouldNotContainMappingInitiallyTest() {
    assertTrue(cache.get(key1, key2).isEmpty());
  }

  @Test
  public void shouldContainMappingAfterPuttingTest() {
    ArtistTitleSearchResult expected = MetallumFileHelper.searchResult;

    cache.put(key1, key2, expected);

    Optional<ArtistTitleSearchResult> opt = cache.get(key1, key2);
    assertTrue(opt.isPresent());

    ArtistTitleSearchResult actual = opt.get();

    assertEquals(expected, actual);
  }

  @Test
  public void shouldNotContainMappingAfterClearingTest() {
    cache.put(key1, key2, expected);

    cache.clear();

    assertTrue(cache.get(key1, key2).isEmpty());
  }
}
