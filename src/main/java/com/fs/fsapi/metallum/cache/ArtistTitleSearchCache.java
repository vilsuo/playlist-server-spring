package com.fs.fsapi.metallum.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

// TODO add "time limit"

@Service
@RequiredArgsConstructor
public class ArtistTitleSearchCache implements MetallumCache<ArtistTitleSearchResult>{

  private final Map<String, Map<String, ArtistTitleSearchResult>> cache;

  @Override
  public void put(String artist, String title, ArtistTitleSearchResult result) {
    if (!cache.containsKey(artist)) {
      cache.put(artist, new HashMap<>());
    }

    cache.get(artist).put(title, result);
  }

  @Override
  public Optional<ArtistTitleSearchResult> get(String artist, String title) {
    if (cache.containsKey(artist)) {
      if (cache.get(artist).containsKey(title)) {
        return Optional.of(cache.get(artist).get(title));
      }
    }

    return Optional.empty();
  }
}
