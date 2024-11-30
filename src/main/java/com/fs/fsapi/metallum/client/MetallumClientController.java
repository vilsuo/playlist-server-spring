package com.fs.fsapi.metallum.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fs.fsapi.metallum.base.MetallumController;
import com.fs.fsapi.metallum.result.SongResult;
import com.fs.fsapi.metallum.result.lyrics.LyricsResult;
import com.fs.fsapi.metallum.result.search.ArtistTitleSearchResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/metallum/client")
@RequiredArgsConstructor
public class MetallumClientController implements MetallumController {

  private final MetallumClientService service;
  
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
  
  /*
  @GetMapping("/logo/{artistId}")
  public ResponseEntity<byte[]> searchArtistLogo(@PathVariable String artistId) {
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(service.searchArtistLogo(artistId));
  }
  */

  @GetMapping("/logo/{artistId}/url")
  public ResponseEntity<String> getArtistLogoUrl(@PathVariable String artistId) {
    return ResponseEntity
      .ok()
      .body(service.getArtistLogoUrl(artistId));
  }

  /*
  @GetMapping("/cover/{titleId}")
  public ResponseEntity<byte[]> searchTitleCover(@PathVariable String titleId) {
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(service.searchTitleCover(titleId));
  }
  */

  @GetMapping("/cover/{titleId}/url")
  public ResponseEntity<String> getTitleCoverUrl(@PathVariable String titleId) {
    return ResponseEntity
      .ok()
      .body(service.getTitleCoverUrl(titleId));
  }
}
