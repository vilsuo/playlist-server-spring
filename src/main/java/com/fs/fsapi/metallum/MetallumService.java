package com.fs.fsapi.metallum;

import java.util.List;

import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

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
}
