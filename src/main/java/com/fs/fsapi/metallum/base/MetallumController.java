package com.fs.fsapi.metallum.base;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fs.fsapi.metallum.result.ResultImage;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MetallumController implements MetallumControllerInterface {

  private final MetallumServiceInterface service;
  
  @Override
  public ResponseEntity<ArtistTitleSearchResult> search(String artist, String title) {
    return ResponseEntity
      .ok()
      .body(service.searchByArtistAndTitle(artist, title));
  }

  @Override
  public ResponseEntity<List<SongResult>> searchSongs(String artistId, String titleId) {
    return ResponseEntity
      .ok()
      .body(service.searchSongs(artistId, titleId));
  }

  @Override
  public ResponseEntity<LyricsResult> searchSongLyrics(String artistId, String titleId, String songId) {
    return ResponseEntity
      .ok()
      .body(service.searchSongLyrics(artistId, titleId, songId));
  }

  // postman can have hard time displaying images: manual test from browser
  @Override
  public ResponseEntity<byte[]> searchArtistLogo(String artistId) {
    final ResultImage img = service.searchArtistLogo(artistId);

    return ResponseEntity
      .ok()
      .contentType(img.getMediaType())
      .body(img.getBytes());
  }

  @Override
  public ResponseEntity<String> getArtistLogoUrl(String artistId) {
    return ResponseEntity
      .ok()
      .body(service.getArtistLogoUrl(artistId));
  }

  // postman can have hard time displaying images: manual test from browser
  @Override
  public ResponseEntity<byte[]> searchTitleCover(String artistId, String titleId) {
    final ResultImage img = service.searchTitleCover(artistId, titleId);

    return ResponseEntity
      .ok()
      .contentType(img.getMediaType())
      .body(img.getBytes());
  }

  @Override
  public ResponseEntity<String> getTitleCoverUrl(String artistId, String titleId) {
    return ResponseEntity
      .ok()
      .body(service.getTitleCoverUrl(artistId, titleId));
  }
}
