package com.fs.fsapi.metallum.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fs.fsapi.helpers.MetallumFileHelper;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

@SpringBootTest(classes = { ArtistTitleSearchCache.class })
public class ArtistTitleSearchCacheTest {
  
  @Autowired
  private ArtistTitleSearchCache cache;

  private final ArtistTitleSearchResult expected = MetallumFileHelper.SEARCH_RESULT;
  private final String key1 = expected.getArtist();
  private final String key2 = expected.getTitle();

  // can not spy a lambda
  private final Supplier<ArtistTitleSearchResult> supplier = new Supplier<ArtistTitleSearchResult>() {
    public ArtistTitleSearchResult get() {
      return expected;
    }
  };

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
    final ArtistTitleSearchResult expected = MetallumFileHelper.SEARCH_RESULT;

    cache.put(key1, key2, expected);

    final Optional<ArtistTitleSearchResult> opt = cache.get(key1, key2);
    assertTrue(opt.isPresent());

    final ArtistTitleSearchResult actual = opt.get();

    assertEquals(expected, actual);
  }

  @Test
  public void shouldNotContainMappingAfterClearingTest() {
    cache.put(key1, key2, expected);

    cache.clear();

    assertTrue(cache.get(key1, key2).isEmpty());
  }

  @Test
  public void shouldCallSupplierWhenNotFoundTest() {
    final Supplier<ArtistTitleSearchResult> spy = spy(supplier);

    cache.getOrElseSupply(key1, key2, spy);

    verify(spy).get();
  }

  @Test
  public void shouldReturnSuppliedValueWhenNotFoundTest() {
    final Supplier<ArtistTitleSearchResult> spy = spy(supplier);

    final ArtistTitleSearchResult actual = cache.getOrElseSupply(key1, key2, spy);

    assertEquals(expected, actual);
  }

  @Test
  public void shouldNotCallSupplierWhenFoundTest() {
    // insert value
    cache.put(key1, key2, expected);

    // find inserted value
    final Supplier<ArtistTitleSearchResult> spy = spy(supplier);
    cache.getOrElseSupply(key1, key2, spy);

    verify(spy, times(0)).get();
  }
}
