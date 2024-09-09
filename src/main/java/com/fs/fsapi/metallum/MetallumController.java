package com.fs.fsapi.metallum;

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
  
  @GetMapping("/songs")
  public ResponseEntity<?> getAll(
    @RequestParam String artist,
    @RequestParam String title
  ) {
    
    return ResponseEntity
      .ok()
      .body(service.getSearchResultsWeb(artist, title));
  }
}
