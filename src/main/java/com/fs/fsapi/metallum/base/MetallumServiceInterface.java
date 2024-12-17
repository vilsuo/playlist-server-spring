package com.fs.fsapi.metallum.base;

import java.util.List;

import com.fs.fsapi.metallum.result.ResultImage;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

public interface MetallumServiceInterface {

  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title);

  public List<SongResult> searchSongs(String artistId, String titleId);

  public LyricsResult searchSongLyrics(String artistId, String titleId, String songId);

  public ResultImage searchArtistLogo(String artistId);

  public String getArtistLogoUrl(String artistId);

  public ResultImage searchTitleCover(String artistId, String titleId);

  public String getTitleCoverUrl(String artistId, String titleId);
}
