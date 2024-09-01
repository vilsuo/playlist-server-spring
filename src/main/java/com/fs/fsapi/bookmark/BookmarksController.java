package com.fs.fsapi.bookmark;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fs.fsapi.bookmark.parser.AlbumBase;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// TODO
// - add file type detection with Apache Tika
//  - with validate with @annotation?
//  - response with 415 (Unsupported Media Type)?

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarksController {

  private final BookmarkService service;
  
  @PostMapping
  public ResponseEntity<List<AlbumBase>> uploadBookmarks(
    @RequestParam MultipartFile file,
    @NotEmpty @RequestParam String name
  ) throws IOException {

    return ResponseEntity
      .ok()
      .body(service.getAlbumBases(file, name));
  }

}
