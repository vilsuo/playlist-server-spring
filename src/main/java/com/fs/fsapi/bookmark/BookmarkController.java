package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService service;
  
  @PostMapping
  public ResponseEntity<List<AlbumBase>> uploadBookmarks(
    @RequestParam MultipartFile file,
    @RequestParam String name
  ) throws IOException {

    return ResponseEntity
      .ok()
      .body(service.getAlbumBases(file, name));
  }
  
}
