package com.fs.fsapi.metallum.base;

import java.util.List;

import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

public interface MetallumParserInterface<T, S, R> {

  public List<ArtistTitleSearchResult> parseSearchResults(T results);

  public List<SongResult> parseSongs(S results);

  public LyricsResult parseLyrics(R results);
}
