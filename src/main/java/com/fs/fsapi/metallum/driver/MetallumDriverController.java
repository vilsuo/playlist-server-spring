package com.fs.fsapi.metallum.driver;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fs.fsapi.metallum.result.ArtistTitleSearchResult;
import com.fs.fsapi.metallum.result.LyricsResult;
import com.fs.fsapi.metallum.result.SongResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/metallum/driver")
@RequiredArgsConstructor
public class MetallumDriverController {

  private final MetallumDriverService service;
  
  @GetMapping("/search")
  public ResponseEntity<ArtistTitleSearchResult> search(
    @RequestParam String artist,
    @RequestParam String title
  ) {
    return ResponseEntity
      .ok()
      .body(service.searchByArtistAndTitle(artist, title));
  }

  @GetMapping("/songs/{titleId}")
  public ResponseEntity<List<SongResult>> searchSongs(
    @PathVariable String titleId
  ) throws InterruptedException {
    return ResponseEntity
      .ok()
      .body(service.searchSongs(titleId));
  }

  @GetMapping("/songs/{titleId}/lyrics/{songId}")
  public ResponseEntity<LyricsResult> searchSongLyrics(
    @PathVariable String titleId,
    @PathVariable String songId
  ) {
    return ResponseEntity
      .ok()
      .body(service.searchSongLyrics(titleId, songId));
  }

  @PostMapping("/cookie")
  public ResponseEntity<Void> postMethodName(@RequestBody String value) {
    service.replaceCookie(value);
    
    return ResponseEntity
      .noContent()
      .build();
  }
  
}
