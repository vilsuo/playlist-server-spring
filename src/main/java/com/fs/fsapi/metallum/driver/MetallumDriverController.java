package com.fs.fsapi.metallum.driver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fs.fsapi.metallum.MetallumImage;
import com.fs.fsapi.metallum.base.MetallumController;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/metallum/driver")
@RequiredArgsConstructor
public class MetallumDriverController implements MetallumController {

  private final MetallumDriverService service;
  
  @Override
  public ResponseEntity<ArtistTitleSearchResult> search(String artist, String title) {
    return ResponseEntity
      .ok()
      .body(service.searchByArtistAndTitle(artist, title));
  }

  @Override
  public ResponseEntity<List<SongResult>> searchSongs(String titleId) {
    return ResponseEntity
      .ok()
      .body(service.searchSongs(titleId));
  }

  @Override
  public ResponseEntity<LyricsResult> searchSongLyrics(String titleId, String songId) {
    return ResponseEntity
      .ok()
      .body(service.searchSongLyrics(titleId, songId));
  }

  @Override
  public ResponseEntity<byte[]> searchArtistLogo(String artistId) {
    final MetallumImage img = service.searchArtistLogo(artistId);

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

  @Override
  public ResponseEntity<byte[]> searchTitleCover(String titleId) {
    final MetallumImage img = service.searchTitleCover(titleId);

    return ResponseEntity
      .ok()
      .contentType(img.getMediaType())
      .body(img.getBytes());
  }

  @Override
  public ResponseEntity<String> getTitleCoverUrl(String titleId) {
    return ResponseEntity
      .ok()
      .body(service.getTitleCoverUrl(titleId));
  }

  @PostMapping("/cookie")
  public ResponseEntity<Void> setCookie(@RequestBody String value) {
    service.setBypassCookie(value);
    
    return ResponseEntity
      .noContent()
      .build();
  }
}
