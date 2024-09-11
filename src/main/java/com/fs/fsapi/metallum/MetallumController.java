package com.fs.fsapi.metallum;

import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fs.fsapi.metallum.parser.SongResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/metallum")
@RequiredArgsConstructor
public class MetallumController {

  private final MetallumService service;
  
  @GetMapping("/search")
  public ResponseEntity<ArtistTitleSearchResult> search(
    @RequestParam String artist,
    @RequestParam String title
  ) {
    return ResponseEntity
      .ok()
      .body(service.searchByArtistAndTitle(artist, title));
  }

  @GetMapping("/logo/{artistId}")
  public ResponseEntity<byte[]> searchArtistLogo(@PathVariable String artistId) {
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(service.searchArtistLogo(artistId));
  }

  @GetMapping("/cover/{titleId}")
  public ResponseEntity<byte[]> searchTitleCover(@PathVariable String titleId) {
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(service.searchTitleCover(titleId));
  }

  @GetMapping("/songs/{titleId}")
  public ResponseEntity<List<SongResult>> searchSongs(@PathVariable String titleId) {
    return ResponseEntity
      .ok()
      .body(service.searchSongs(titleId));
  }

  @GetMapping("/lyrics/{songId}")
  public ResponseEntity<String> searchSongLyrics(@PathVariable String songId) {
    return ResponseEntity
      .ok()
      .body(service.searchSongLyrics(songId));
  }
  
}
