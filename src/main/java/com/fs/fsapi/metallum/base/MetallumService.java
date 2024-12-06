package com.fs.fsapi.metallum.base;

import java.util.List;

import com.fs.fsapi.metallum.MetallumImage;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

public interface MetallumService {

  /**
   * Search for basic release information.
   * 
   * @param artist  the artist name
   * @param title  the release title
   * @return search result
   */
  public ArtistTitleSearchResult searchByArtistAndTitle(String artist, String title);

  /**
   * Search songs of a release.
   * 
   * @param titleId  the release title id
   * @return release song list
   */
  public List<SongResult> searchSongs(String titleId);

   /**
   * Search song lyrics.
   * 
   * @param songId  the release title id
   * @param songId  the song id
   * @return the song lyrics
   */
  public LyricsResult searchSongLyrics(String titleId, String songId);

  /**
   * Get artist logo image.
   * 
   * @param artistId  the artist id
   * @return the image
   */
  public MetallumImage searchArtistLogo(String artistId);

  /**
   * Get release title cover image.
   * 
   * @param titleId  the release title id
   * @return the image
   */
  public MetallumImage searchTitleCover(String titleId);

  /**
   * Get the url where the artist logo image can be found.
   * 
   * @param id  the artist id
   * @return the image url
   */
  public String getArtistLogoUrl(String id);

  /**
   * Get the url where the release title cover image can be found.
   * 
   * @param id  the release title id
   * @return the image url
   */
  public String getTitleCoverUrl(String id);
}
