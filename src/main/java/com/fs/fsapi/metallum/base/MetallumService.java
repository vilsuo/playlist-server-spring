package com.fs.fsapi.metallum.base;

import java.util.List;

import com.fs.fsapi.metallum.cache.ArtistTitleSearchCache;
import com.fs.fsapi.metallum.result.ResultImage;
import com.fs.fsapi.metallum.result.ResultRanker;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MetallumService<T, S, R> implements MetallumServiceInterface {

  private final MetallumWebInterface<T, S, R> web;

  private final MetallumParserInterface<T, S, R> parser;

  private final ArtistTitleSearchCache cache;

  private final ResultRanker ranker;

  @Override
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title) {
    return cache.getOrElseSupply(artist, title, () -> {
      final T response = web.getSearchResponse(artist, title);
      final List<ArtistTitleSearchResult> results = parser.parseSearchResults(response);
      
      final ArtistTitleSearchResult result = ranker
        .getBestSearchResult(results, artist, title);

      cache.put(artist, title, result);

      return result;
    });
  }

  @Override
  public List<SongResult> searchSongs(String artistId, String titleId) {
    return parser.parseSongs(web.getSongs(titleId));
  }

  @Override
  public LyricsResult searchSongLyrics(String artistId, String titleId, String songId) {
    return parser.parseLyrics(web.getSongLyrics(titleId, songId));
  }

  @Override
  public ResultImage searchArtistLogo(String artistId) {
    return web.getArtistLogo(artistId);
  }

  @Override
  public String getArtistLogoUrl(String artistId) {
    return web.getArtistLogoUrl(artistId);
  }

  @Override
  public ResultImage searchTitleCover(String artistId, String titleId) {
    return web.getTitleCover(titleId);
  }

  @Override
  public String getTitleCoverUrl(String artistId, String titleId) {
    return web.getTitleCoverUrl(titleId);
  }
}
