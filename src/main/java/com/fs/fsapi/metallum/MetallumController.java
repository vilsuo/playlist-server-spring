package com.fs.fsapi.metallum;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/metallum")
@RequiredArgsConstructor
public class MetallumController {

  private final MetallumService service;
  
  @GetMapping("/links")
  public ResponseEntity<?> getLinks(
    @RequestParam String artist,
    @RequestParam String title
  ) {

    ArtistTitleSearchResult s = service.searchWithArtistAndTitle(artist, title);

    return ResponseEntity
      .ok()
      .body(new String[]{ s.getArtistHref(), s.getTitleHref() });
  }

  @GetMapping("/cover")
  public ResponseEntity<?> getCover(
    @RequestParam String artist,
    @RequestParam String title
  ) {

    byte[] image = service.searchCover(artist, title);

    return ResponseEntity
      .ok()
      .contentType(MediaType.IMAGE_JPEG)
      .body(image);
  }
}
