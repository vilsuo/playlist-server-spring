package com.fs.fsapi.metallum.client;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fs.fsapi.metallum.base.MetallumService;
import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.response.ArtistTitleSearchResponse;
import com.fs.fsapi.metallum.result.ResultRanker;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

// TODO
// - handle WebClientResponseException
// - handle not found cases, etc...

@Service
@RequiredArgsConstructor
public class MetallumClientService implements MetallumService {

  private final MetallumClient client;

  private final MetallumClientParser parser;

  private final ArtistTitleSearchCache cache;

  private final ResultRanker ranker;

  @Override
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    return cache.getOrElseSupply(artist, title, () -> {
      final ArtistTitleSearchResponse response = client.loadSearchResponse(artist, title);
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
    return parser.parseSongs(client.loadSongs(titleId));
  }

  // title id not needed
  @Override
  public LyricsResult searchSongLyrics(String titleId, String songId) {
    return parser.parseLyrics(client.loadSongLyrics(songId));
  }

  @Override
  public byte[] searchArtistLogo(String id) {
    return client.loadArtistLogo(id);
  }

  @Override
  public byte[] searchTitleCover(String id) {
    return client.loadTitleCover(id);
  }

  @Override
  public String getArtistLogoUrl(String id) {
    return client.createArtistLogoUrl(id);
  }

  @Override
  public String getTitleCoverUrl(String id) {
    return client.createTitleCoverUrl(id);
  }
}
