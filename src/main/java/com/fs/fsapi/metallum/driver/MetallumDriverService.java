package com.fs.fsapi.metallum.driver;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.base.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.result.ResultRanker;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetallumDriverService implements MetallumService {

  private final MetallumDriver driver;

  private final MetallumDriverParser parser;

  private final ArtistTitleSearchCache cache;

  private final ResultRanker ranker;

  @Override
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    return cache.getOrElseSupply(artist, title, () -> {
      final String response = driver.getSearchTableBody(artist, title);
      final List<ArtistTitleSearchResult> results = parser.parseSearchResults(response);
      
      // find the "best" result
      final ArtistTitleSearchResult result = ranker
        .getBestSearchResult(results, artist, title);

      // update cache
      cache.put(artist, title, result);

      return result;
    });
  }

  @Override
  public List<SongResult> searchSongs(String titleId) {
    return parser.parseSongs(driver.getSongsContainer(titleId));
  }

  @Override
  public LyricsResult searchSongLyrics(String titleId, String songId) {
    return parser.parseLyrics(driver.getLyricsContainer(titleId, songId));
  }

  public void setBypassCookie(String value) {
    driver.setBypassCookie(value);
  }
}
