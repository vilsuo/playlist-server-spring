package com.fs.fsapi.metallum.base;

import java.util.List;

import com.fs.fsapi.metallum.result.ResultImage;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

public interface MetallumServiceInterface {

  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title);

  public List<SongResult> searchSongs(String titleId);

  public LyricsResult searchSongLyrics(String titleId, String songId);

  public ResultImage searchArtistLogo(String artistId);

  public ResultImage searchTitleCover(String titleId);

  public String getArtistLogoUrl(String artistIdString);

  public String getTitleCoverUrl(String titleIdString);
}
