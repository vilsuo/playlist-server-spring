package com.fs.fsapi.metallum.driver;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.base.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.result.ResultRanker;

@Service
public class MetallumDriverService extends MetallumService<String, String, String> {

  private final MetallumDriver driver;

  public MetallumDriverService(
    MetallumDriver driver,
    MetallumDriverParser parser,
    ArtistTitleSearchCache cache,
    ResultRanker ranker
  ) {
    super(driver, parser, cache, ranker);
    this.driver = driver;
  }

  public void setBypassCookie(String value) {
    driver.setBypassCookie(value);
  }
}
