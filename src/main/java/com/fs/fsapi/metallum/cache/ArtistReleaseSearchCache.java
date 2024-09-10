package com.fs.fsapi.metallum.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.ArtistReleaseSearchResult;

import lombok.RequiredArgsConstructor;

// TODO add "time limit"?

@Service
@RequiredArgsConstructor
public class ArtistReleaseSearchCache implements DoubleKeyMap<String, String, ArtistReleaseSearchResult> {

  private final Map<String, Map<String, ArtistReleaseSearchResult>> cache;

  @Override
  public void put(String artist, String title, ArtistReleaseSearchResult result) {
    if (!cache.containsKey(artist)) {
      cache.put(artist, new HashMap<>());
    }

    cache.get(artist).put(title, result);
  }

  @Override
  public Optional<ArtistReleaseSearchResult> get(String artist, String title) {
    if (cache.containsKey(artist)) {
      if (cache.get(artist).containsKey(title)) {
        return Optional.of(cache.get(artist).get(title));
      }
    }

    return Optional.empty();
  }

  @Override
  public void clear() {
    cache.clear();
  }
}
