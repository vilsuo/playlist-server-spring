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
  
  @GetMapping("/links")
  public ResponseEntity<ArtistTitleSearchResult> getLinks(
    @RequestParam String artist,
    @RequestParam String title
  ) {
    return ResponseEntity
      .ok()
      .body(service.searchByArtistAndReleaseTitle(artist, title));
  }

  @GetMapping("/logo")
  public ResponseEntity<byte[]> getArtistLogo(
    @RequestParam String artist,
    @RequestParam String title
  ) {
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(service.searchArtistLogo(artist, title));
  }

  @GetMapping("/cover")
  public ResponseEntity<byte[]> getReleaseCover(
    @RequestParam String artist,
    @RequestParam String title
  ) {
    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(service.searchReleaseCover(artist, title));
  }

  @GetMapping("/songs")
  public ResponseEntity<List<SongResult>> getSongs(
    @RequestParam String artist,
    @RequestParam String title
  ) throws URISyntaxException {
    return ResponseEntity
      .ok()
      .body(service.searchSongs(artist, title));
  }

  @GetMapping("/songs/{id}/lyrics")
  public ResponseEntity<String> getSongLyrics(@PathVariable String id) {
    return ResponseEntity
      .ok()
      .body(service.searchSongLyrics(id));
  }
  
}
